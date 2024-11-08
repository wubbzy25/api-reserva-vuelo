package com.api.reservavuelos.Filters;

import com.api.reservavuelos.Exceptions.JwtTokenMissingException;
import com.api.reservavuelos.Security.JwtTokenProvider;
import com.api.reservavuelos.Security.getTokenForRequest;
import com.api.reservavuelos.Utils.Url_WhiteList;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class JwtValidationFilterTest {

    @Mock
    private Url_WhiteList urlWhiteList;

    @Mock
    private JwtTokenProvider jwtTokenProvider;


    @Mock
    private HandlerExceptionResolver resolver;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;
    @Mock
    getTokenForRequest getTokenForRequest;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtValidationFilter jwtValidationFilter;

    @Test
    void testFilterAllowsWhitelistedUrls() throws ServletException, IOException {
        // Configurar la URL en la lista blanca
        when(request.getRequestURI()).thenReturn("/public-url");
        when(urlWhiteList.Url_whiteList()).thenReturn(Arrays.asList("/public-url"));

        jwtValidationFilter.doFilterInternal(request, response, filterChain);

        // Verificar que la cadena de filtros continúa sin validar el token
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtTokenProvider, never()).IsValidToken(anyString());
    }

    @Test
    void testFilterValidToken() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/protected-url");
        when(urlWhiteList.Url_whiteList()).thenReturn(Arrays.asList("/public-url"));
        when(getTokenForRequest.getToken(request)).thenReturn("valid-token");

        jwtValidationFilter.doFilterInternal(request, response, filterChain);

        // Verificar que el token se valida y continúa con la cadena de filtros
        verify(jwtTokenProvider, times(1)).IsValidToken("valid-token");
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testFilterThrowsJwtTokenMissingExceptionWhenTokenIsEmpty() {

        when(request.getRequestURI()).thenReturn("/protected-url");
        when(urlWhiteList.Url_whiteList()).thenReturn(Collections.singletonList("/public-url"));


        when(getTokenForRequest.getToken(request)).thenReturn("");


        assertThrows(JwtTokenMissingException.class, () ->
                jwtValidationFilter.doFilterInternal(request, response, filterChain)
        );


        verify(getTokenForRequest, times(1)).getToken(request);

        verify(resolver, times(1)).resolveException(eq(request), eq(response), isNull(), any(JwtTokenMissingException.class));
    }

    @Test
    void testFilterHandlesExpiredJwtException() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/protected-url");
        when(urlWhiteList.Url_whiteList()).thenReturn(Arrays.asList("/public-url"));
        when(getTokenForRequest.getToken(request)).thenReturn("expired-token");
        doThrow(ExpiredJwtException.class).when(jwtTokenProvider).IsValidToken("expired-token");

        jwtValidationFilter.doFilterInternal(request, response, filterChain);

        // Verificar que se maneja la excepción y se llama al resolver
        verify(resolver, times(1)).resolveException(eq(request), eq(response), isNull(), any(ExpiredJwtException.class));
    }

    @Test
    void testFilterHandlesMalformedJwtException() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/protected-url");
        when(urlWhiteList.Url_whiteList()).thenReturn(Arrays.asList("/public-url"));
        when(getTokenForRequest.getToken(request)).thenReturn("malformed-token");
        doThrow(MalformedJwtException.class).when(jwtTokenProvider).IsValidToken("malformed-token");

        jwtValidationFilter.doFilterInternal(request, response, filterChain);

        // Verificar que se maneja la excepción y se llama al resolver
        verify(resolver, times(1)).resolveException(eq(request), eq(response), isNull(), any(MalformedJwtException.class));
    }
}