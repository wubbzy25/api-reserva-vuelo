package com.api.reservavuelos.Security;

//importamos las librerias necesarias
import com.api.reservavuelos.DTO.Response.ResponseDTO;
import com.api.reservavuelos.Utils.DateFormatter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

//definimos la clase jwtAuthenticationEntryPoint que hereda de AuthenticationEntryPoint y le colocamos la anotacion @Component para que spring lo reconozca
@Component
public class jwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    //declaramos los servicios, repositorios o interfaces necesarios para utilizarlos
    //componente para obtener la fecha formateada
    private final DateFormatter dateFormatter;
    //aplicamos inyeccion de dependencias por medio del contructor
    @Autowired
    public jwtAuthenticationEntryPoint(DateFormatter dateFormatter) {
        this.dateFormatter = dateFormatter;
    }
    //sobreescribimos el metodo commence para que se realice la peticion de error al usuario que no esta autorizado para realizar la peticion
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setTimeStamp(dateFormatter.formatearFecha());
        responseDTO.setCode("P-403");
        responseDTO.setMessage("No estas autorizado para realizar esta accion");
        responseDTO.setUrl(request.getRequestURI());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseDTO));
    }
}
