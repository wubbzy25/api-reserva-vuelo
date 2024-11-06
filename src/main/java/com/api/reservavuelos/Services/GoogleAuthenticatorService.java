package com.api.reservavuelos.Services;

// Importamos las librerías necesarias
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.IGoogleAuthenticator;
import org.springframework.stereotype.Service;

// Definimos la clase GoogleAuthenticatorService y la anotamos con @Service para que Spring la reconozca como un servicio
@Service
public class GoogleAuthenticatorService {
    // Declaramos el GoogleAuthenticator
    private  IGoogleAuthenticator gAuth = new GoogleAuthenticator();

    // Método para generar una clave secreta
    public String generateSecretKey() {
        // Creamos las credenciales del autenticador de Google
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        // Devolvemos la clave secreta generada
        return key.getKey();
    }

    // Método para validar el código
    public boolean validateCode(String secretKey, int code) {
        // Autorizamos el código utilizando la clave secreta
        return gAuth.authorize(secretKey, code);
    }
}
