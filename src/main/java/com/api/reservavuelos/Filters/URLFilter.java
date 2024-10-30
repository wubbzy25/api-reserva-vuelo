package com.api.reservavuelos.Filters;

// Importamos las clases necesarias
import com.api.reservavuelos.DTO.Response.ResponseDTO;
import com.api.reservavuelos.Exceptions.MethodNotAllowedException;
import com.api.reservavuelos.Exceptions.UrlNotFoundException;
import com.api.reservavuelos.Utils.DateFormatter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.*;

// Definimos la clase URLFilter que extiende OncePerRequestFilter y la anotamos con @Component para que Spring la reconozca como un bean
@Component
public class URLFilter extends OncePerRequestFilter {

    // Objeto para mapear datos a JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Inyectamos el DateFormatter
    @Autowired
    private DateFormatter dateFormatter;

    // Mapa estático para almacenar las URLs válidas y sus métodos HTTP
    private static final Map<String, String> URLS_MAP = new HashMap<>();

    static {
        // Definimos las URLs y los métodos HTTP permitidos
        URLS_MAP.put("/api/v1/auth/register", "POST");
        URLS_MAP.put("/api/v1/auth/login", "POST");
        URLS_MAP.put("/api/v1/auth/forgot-password", "GET");
        URLS_MAP.put("/api/v1/auth/verify-code", "POST");
        URLS_MAP.put("/api/v1/auth/change-password", "POST");
        URLS_MAP.put("/api/v1/auth/2FA/setup", "GET");
        URLS_MAP.put("/api/v1/auth/2FA/verify", "POST");
        URLS_MAP.put("/api/v1/profile/upload-image", "POST");
        URLS_MAP.put("/api/v1/profile/\\d+", "GET");
        URLS_MAP.put("/api/v1/profile/edit-profile/\\d+", "PUT");
        URLS_MAP.put("/api/v1/vuelos", "GET");
        URLS_MAP.put("/api/v1/vuelos/vuelo/\\d+", "GET");
        URLS_MAP.put("/api/v1/vuelos/vuelo/asientos/\\d+", "GET");
        URLS_MAP.put("/api/v1/vuelos/vuelo/crear", "POST");
        URLS_MAP.put("/api/v1/vuelos/vuelo/actualizar-estado/\\d+", "PUT");
        URLS_MAP.put("/api/v1/vuelos/vuelo/editar/\\d+", "PUT");
        URLS_MAP.put("/api/v1/vuelos/vuelo/eliminar/\\d+", "DELETE");
        URLS_MAP.put("/api/v1/reservas/reservar/\\d+", "POST");
        URLS_MAP.put("/api/v1/reservas/cancelar/\\d+", "DELETE");
    }

    // Método para manejar excepciones y enviar una respuesta en formato JSON
    private void ExceptionHandler(HttpServletResponse response, String code, String message, String requestURI) throws IOException {
        String formattedDate = dateFormatter.formatearFecha();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setCode(code);
        responseDTO.setMessage(message);
        responseDTO.setUrl(requestURI);
        responseDTO.setTimeStamp(formattedDate);
        String jsonResponse = objectMapper.writeValueAsString(responseDTO);
        response.getWriter().write(jsonResponse);
    }

    // Método principal del filtro
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();
        boolean urlMatched = false;

        try {
            // Verificamos si la URL y el método HTTP coinciden con los permitidos
            for (Map.Entry<String, String> entry : URLS_MAP.entrySet()) {
                if (requestURI.matches(entry.getKey()) && requestMethod.equals(entry.getValue())) {
                    urlMatched = true;
                    break;
                }
            }

            // Si no coinciden, lanzamos una excepción de URL no encontrada
            if (!urlMatched) {
                throw new UrlNotFoundException();
            }

            // Continuamos con la cadena de filtros
            filterChain.doFilter(request, response);
        } catch (UrlNotFoundException | MethodNotAllowedException e) {
            // Si se produce una excepción, manejamos la respuesta de error
            ExceptionHandler(response, "404", "URL no Existe :/", requestURI);
        }
    }
}
