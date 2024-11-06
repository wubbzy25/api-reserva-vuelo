package com.api.reservavuelos.Services;

import com.api.reservavuelos.DataProvider;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.IGoogleAuthenticator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleAuthenticatorServiceTest {

    @Mock
    private IGoogleAuthenticator gAuth;

    @InjectMocks
    GoogleAuthenticatorService googleAuthenticatorService;


    @Test
    void generateSecretKey() {


        GoogleAuthenticatorKey mockKey = new GoogleAuthenticatorKey.Builder("key")
                .setKey("key")
                .build();
        when(gAuth.createCredentials()).thenReturn(mockKey);

        assertEquals("key", googleAuthenticatorService.generateSecretKey());

        verify(gAuth, times(1)).createCredentials();
    }

    @Test
    void validateCode() {

        when(gAuth.authorize(anyString(), anyInt())).thenReturn(true);

        assertTrue(googleAuthenticatorService.validateCode("key", 123456));

        verify(gAuth, times(1)).authorize(anyString(), anyInt());
    }
}