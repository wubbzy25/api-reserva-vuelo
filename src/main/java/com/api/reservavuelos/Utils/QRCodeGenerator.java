package com.api.reservavuelos.Utils;

//importamos las librerias necesarias
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

//definimos la clase QRCodeGenerator y le colocamos @component para que spring lo reconozca
@Component
public class QRCodeGenerator {

    //definimos el metodo que generara el link del codigo QR
    public String getQRCodeURL(Long id_usuario, String secret){
    try {
    String format = "otpauth://totp/%s@%s?secret=%s";
        return String.format(format, URLEncoder.encode(String.valueOf(id_usuario), StandardCharsets.UTF_8),
                URLEncoder.encode("reserva api vuelos", StandardCharsets.UTF_8), secret);

    } catch (Exception e){
     throw new RuntimeException(e);
    }
    }

    //definimos el metodo que generara la imagen del codigo QR
    public  byte[] generateQRCodeImage(String barcodeText) throws WriterException, IOException {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 200, 200);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }
}
