package com.api.reservavuelos.Services;

// Importamos las librerías necesarias para enviar correos electrónicos
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

// Definimos la clase EmailSenderService y la anotamos con @Service para que Spring la reconozca como un servicio
@Service
public class EmailSenderService {

    // Obtenemos el nombre de usuario del email desde las propiedades de configuración
    @Value("${spring.mail.username}")
    private String email;

    // Declaramos el JavaMailSender para enviar correos electrónicos
    private final JavaMailSender javaMailSender;

    // Constructor para inyectar JavaMailSender
    @Autowired
    public EmailSenderService(JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }

    // Método para enviar un correo de restablecimiento de contraseña de forma asíncrona
    @Async
    public void sendRestPasswordEmail(String from, String code){
        // Contenido del correo electrónico
        String emailContent = "Hola! Te hemos enviado un código para restablecer tu contraseña.\n" +
                "Aquí tienes el código para restablecer tu contraseña: " + code + "\n" +
                "Si no has solicitado este cambio, no te preocupes, tu contraseña seguirá siendo segura.\n" +
                "Gracias por utilizar nuestro sistema!";

        MimeMessage message = javaMailSender.createMimeMessage();

        try{
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(email);
            helper.setTo(from);
            helper.setSubject("Restablecimiento de contraseña");
            helper.setText(emailContent);
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Método para enviar un correo electrónico con un código QR de forma asíncrona
    @Async
    public void sendEmailWithQRCode(String from, byte[] qrCodeImage){
        MimeMessage message = javaMailSender.createMimeMessage();

        try{
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(email);
            helper.setTo(from);
            helper.setSubject("Setear 2FA");
            helper.setText("Escanea el siguiente código de barra para activar 2FA");
            helper.addAttachment("QrCode2FA.png", new ByteArrayResource(qrCodeImage));
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
