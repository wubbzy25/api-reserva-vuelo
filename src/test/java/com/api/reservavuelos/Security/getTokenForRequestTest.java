package com.api.reservavuelos.Security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class getTokenForRequestTest {
    @Mock
    HttpServletRequest request;
    @InjectMocks
    getTokenForRequest getTokenForRequest;
    @Test
    void getToken() {
        String token = "Bearer 12345";
        when(request.getHeader("Authorization")).thenReturn(token);
        assertEquals("12345", getTokenForRequest.getToken(request));
    }
}