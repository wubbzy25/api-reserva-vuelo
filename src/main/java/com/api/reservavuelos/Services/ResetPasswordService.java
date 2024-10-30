package com.api.reservavuelos.Services;

// Importamos las librerías necesarias
import com.api.reservavuelos.Utils.GenerateCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

// Definimos la clase ResetPasswordService y le colocamos @Service para que Spring lo reconozca como un servicio
@Service
public class ResetPasswordService {
    // Declaramos los servicios, repositorios o interfaces necesarios para utilizarlos

    // Componente para generar los códigos de verificación
    private final GenerateCodes generateCodes;

    // Clase para manejar la base de datos Redis
    private final RedisTemplate<String, Object> redisTemplate;

    // Constructor para inyectar las dependencias de GenerateCodes y RedisTemplate
    @Autowired
    public ResetPasswordService(GenerateCodes generateCodes, RedisTemplate<String, Object> redisTemplate) {
        this.generateCodes = generateCodes;
        this.redisTemplate = redisTemplate;
    }

    // Metodo para establecer el código de reinicio de contraseña
    public String SetResetCode(String email) {
        // Generamos un código de verificación
        String code = String.valueOf(generateCodes.code());
        // Guardamos el código en Redis con una validez de 15 minutos
        redisTemplate.opsForValue().set(email, code, 15, TimeUnit.MINUTES);
        return code; // Devolvemos el código generado
    }

    // Metodo para obtener los datos guardados en Redis usando el email como clave
    public String getData(String email) {
        return (String) redisTemplate.opsForValue().get(email); // Obtenemos el valor asociado al email
    }

    // Metodo para eliminar los datos guardados en Redis usando el email como clave
    public void deleteData(String email) {
        redisTemplate.delete(email); // Eliminamos el valor asociado al email
    }

    // Metodo para establecer el estado de verificación en Redis
    public void setVerifyStatus(String email) {
        // Establecemos el estado de "verified" en Redis con una validez de 5 minutos
        redisTemplate.opsForValue().set(email, "verified", 5, TimeUnit.MINUTES);
    }
}
