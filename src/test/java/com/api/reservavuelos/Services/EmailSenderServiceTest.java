package com.api.reservavuelos.Services;

import com.api.reservavuelos.DataProvider;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailSenderServiceTest {

    @Mock
    JavaMailSender javaMailSender;
    @InjectMocks
    EmailSenderService emailSenderService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailSenderService, "email", "noreply@example.com");
    }



    @Test
    void testSendRestPasswordEmail() {
        // Datos de prueba
        String from = "test@example.com";
        String code = "123456";

        // Crear un MimeMessage simulado
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Simular el envío del correo
        doNothing().when(javaMailSender).send(mimeMessage);

        // Llamar al método que estamos probando
        assertDoesNotThrow(() -> emailSenderService.sendRestPasswordEmail(from, code));

        // Verificar que se ha creado un MimeMessage y se ha enviado
        verify(javaMailSender, times(1)).createMimeMessage();
        verify(javaMailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendRestPasswordEmailThrowsException() throws Exception {
        String from = "test@gmail.com";
        String code = "123456";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(new RuntimeException()).when(javaMailSender).send(mimeMessage);


        assertThrows(RuntimeException.class, () -> {
            emailSenderService.sendRestPasswordEmail(from, code);
        });

        verify(javaMailSender, times(1)).createMimeMessage();
        verify(javaMailSender, times(1)).send(mimeMessage);
    }

    @Test
    void LanzarExcepcionCuandoOcurreUnerroAlEnviarEmailConQRCode(){
        String from = "test@gmail.com";
        byte[] qrCodeImage = DataProvider.obtenerImagenQR();

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(new RuntimeException()).when(javaMailSender).send(mimeMessage);

        assertThrows(RuntimeException.class, () -> {
            emailSenderService.sendEmailWithQRCode(from, qrCodeImage);
        });

        verify(javaMailSender, times(1)).createMimeMessage();
        verify(javaMailSender, times(1)).send(mimeMessage);
    }

    @Test
    void sendEmailWithQRCode() {
        String from = "test@gmail.com";
        byte[] qrCodeImage = DataProvider.obtenerImagenQR();

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(javaMailSender).send(mimeMessage);

        assertDoesNotThrow(() -> emailSenderService.sendEmailWithQRCode(from, qrCodeImage));

        verify(javaMailSender, times(1)).createMimeMessage();
    }
}