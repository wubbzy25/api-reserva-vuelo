package com.api.reservavuelos.Filters;

// Importamos las clases necesarias para la validación del JWT
import com.api.reservavuelos.Exceptions.JwtTokenMissingException;
import com.api.reservavuelos.Security.JwtTokenProvider;
import com.api.reservavuelos.Security.getTokenForRequest;
import com.api.reservavuelos.Utils.DateFormatter;
import com.api.reservavuelos.Utils.Url_WhiteList;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import java.io.IOException;
import java.util.Date;

// Anotamos la clase con @Component para que Spring la reconozca como un bean y @EqualsAndHashCode y @Data para generar métodos comunes
@EqualsAndHashCode(callSuper = true)
@Component
@Data
public class JwtValidationFilter extends OncePerRequestFilter {

    // Instancia de Date para obtener la fecha actual
    private Date tiempoactual = new Date();

    // Inyectamos las dependencias necesarias
    @Autowired
    private DateFormatter dateFormatter;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Autowired
    private Url_WhiteList urlWhiteList;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private getTokenForRequest GetTokenForRequest;

    // Método principal del filtro para la validación del JWT
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Obtenemos la URI de la solicitud
        String requestURI = request.getRequestURI();

        // Verificamos si la URL está en la lista blanca
        if (urlWhiteList.Url_whiteList().contains(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Obtenemos el token de la solicitud
            String token = GetTokenForRequest.getToken(request);

            // Verificamos si el token está presente
            if (token == null || token.isEmpty()) {
                throw new JwtTokenMissingException("El token no puede estar vacío");
            }

            // Validamos el token
            jwtTokenProvider.IsValidToken(token);

            // Continuamos con la cadena de filtros si el token es válido
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException | MalformedJwtException | JwtTokenMissingException e) {
            // Manejamos las excepciones y resolvemos el error utilizando el resolver
            resolver.resolveException(request, response, null, e);
        }
    }
}
