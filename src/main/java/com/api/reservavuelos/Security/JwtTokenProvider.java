package com.api.reservavuelos.Security;

//importamos las librerias necesarias
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

//definimos la clase JwtTokenProvider que implementa la interfaz JwtTokenProvider y le colocamos la anotacion @Component para que spring lo reconozca
@Component
public class JwtTokenProvider {

    //metodo para generar el token
    public String generateToken(Authentication authentication) {
        // obtenemos el nombre del usuario autenticado y lo asignamos a una variable
        String username = authentication.getName();
        //obtenemos la fecha actual
        Date tiempoactual = new Date();
        //sumamos el tiempo actual con el tiempo de expiracion en ConstantSecurity.JWT_EXPIRATION_TOKEN
        Date expiracion = new Date(tiempoactual.getTime() + ConstantSecurity.JWT_EXPIRATION_TOKEN);

        //Generamos el token de JWT
        String Token = Jwts.builder()
                .subject(username)
                .issuedAt(tiempoactual)
                .expiration(expiracion)
                .signWith(SignatureAlgorithm.HS512, ConstantSecurity.JWT_FIRMA)
                .compact();
        return Token;
    }
    //metodo para obtener el email del token
    public String getEmailFromToken(String token) {
        //obtenemos el payload del token
        Claims claims = Jwts.parser()
                .setSigningKey(ConstantSecurity.JWT_FIRMA)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        //retornamos el email del token
      return claims.getSubject();
    }
    //metodo para validar el token
    public void IsValidToken(String token) {
        //obtenemos el payload del token y verificamos la firma del token
           Jwts.parser()
           .setSigningKey(ConstantSecurity.JWT_FIRMA)
           .build()
           .parseClaimsJws(token);
    }
}
