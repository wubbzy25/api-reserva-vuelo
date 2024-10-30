package com.api.reservavuelos.Security;

//importamos las librerias necesarias
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

//definimos la clase getTokenForRequest y le colocamos la anotacion @Component para que spring lo reconozca
@Component
public class getTokenForRequest {

    //definimos el metodo getToken que retorna el token de la peticion
    public String getToken(HttpServletRequest request, HttpServletResponse response){
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
