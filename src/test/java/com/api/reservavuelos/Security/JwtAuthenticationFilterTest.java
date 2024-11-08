package com.api.reservavuelos.Security;

import com.api.reservavuelos.Models.Roles;
import com.api.reservavuelos.Utils.Url_WhiteList;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private getTokenForRequest getTokenForRequest;

    @Mock
    private HandlerExceptionResolver resolver;

    @Mock
    private Url_WhiteList urlWhiteList;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    @Test
    void DoInternalFilterUserAuthenticatedTest() throws ServletException, IOException {
        String token = "validToken";
        String email = "user@example.com";

        // Mocking behavior
        when(getTokenForRequest.getToken(request)).thenReturn(token);
        when(jwtTokenProvider.getEmailFromToken(token)).thenReturn(email);
        UserDetails userDetails = mock(UserDetails.class);
        when(customUserDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        Collection<GrantedAuthority> roles = Collections.singletonList(new SimpleGrantedAuthority("usuario"));
        when(userDetails.getAuthorities()).thenReturn((Collection) roles);

        // Mock SecurityContext
        var securityContext = mock(SecurityContextHolder.getContext().getClass());
        SecurityContextHolder.setContext(securityContext);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

       verify(filterChain, times(1)).doFilter(request, response);
       verify(SecurityContextHolder.getContext(), times(1)).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void DoFilterInternalWhiteListURL() throws ServletException, IOException {
        String requestURI = "/public-endpoint";
        when(request.getRequestURI()).thenReturn(requestURI);
        when(urlWhiteList.Url_whiteList()).thenReturn(List.of("/public-endpoint"));
        var securityContext = mock(SecurityContextHolder.getContext().getClass());
        SecurityContextHolder.setContext(securityContext);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);


        verify(filterChain, times(1)).doFilter(request, response);
        verify(SecurityContextHolder.getContext(), never()).setAuthentication(any());
    }
}