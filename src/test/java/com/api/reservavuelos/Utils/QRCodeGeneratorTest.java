package com.api.reservavuelos.Utils;

import com.google.zxing.WriterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class QRCodeGeneratorTest {

    private QRCodeGenerator qrCodeGenerator;

    @BeforeEach
    void setUp() {
        qrCodeGenerator = new QRCodeGenerator();
    }

    @DisplayName("Comprueba que se genera una URL para el código QR con los parámetros correctos.")
    @Test
    void testGetQRCodeURL() {
        Long userId = 123L;
        String secret = "MYSECRET";

        String expectedUrl = "otpauth://totp/123@reserva+api+vuelos?secret=MYSECRET";
        String actualUrl = qrCodeGenerator.getQRCodeURL(userId, secret);

        assertEquals(expectedUrl, actualUrl, "La URL del código QR no coincide con la esperada.");
    }

    @DisplayName("Comprueba que se genera una imagen del código QR con los parámetros correctos.")
    @Test
    void testGenerateQRCodeImage() throws WriterException, IOException {
        String barcodeText = "Hello, World!";
        byte[] qrCodeImage = qrCodeGenerator.generateQRCodeImage(barcodeText);

        assertNotNull(qrCodeImage, "La imagen del código QR no debería ser nula.");
        assertTrue(qrCodeImage.length > 0, "La imagen del código QR debería tener contenido.");
    }
}
