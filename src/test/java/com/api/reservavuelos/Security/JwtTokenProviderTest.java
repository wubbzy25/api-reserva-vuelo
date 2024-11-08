package com.api.reservavuelos.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @Mock
    Authentication authentication;
    @Mock
    ConstantSecurity constantSecurity;
    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;
    @Test
    void generateToken() {
        String username = "user@example.com";
        when(authentication.getName()).thenReturn(username);

        // Llamamos al método a probar
        String token = jwtTokenProvider.generateToken(authentication);

        // Verificamos que el token no sea nulo
        assertNotNull(token);
        assertTrue(token.startsWith("eyJ"));
    }

    @Test
    void getEmailFromToken() {
        // Crear un token de ejemplo
        String token = Jwts.builder()
                .setSubject("user@example.com")
                .signWith(SignatureAlgorithm.HS512, constantSecurity.JWT_FIRMA)
                .compact();

        // Llamamos al método a probar
        String email = jwtTokenProvider.getEmailFromToken(token);

        // Verificamos el valor del email
        assertEquals("user@example.com", email);
    }

    @Test
    void isValidToken() {
        when(authentication.getName()).thenReturn("test@example.com");
        String token = jwtTokenProvider.generateToken(authentication);

        assertDoesNotThrow(() -> jwtTokenProvider.IsValidToken(token));
       }
}