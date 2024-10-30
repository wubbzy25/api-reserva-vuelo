package com.api.reservavuelos.Services;

// Importamos las librerías necesarias para manejar archivos y Cloudinary
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

// Definimos la clase CloudinaryService y la anotamos con @Service para que Spring la reconozca como un servicio
@Service
public class CloudinaryService {
    // Declaramos una instancia de Cloudinary
    private final Cloudinary cloudinary;

    // Constructor para inyectar Cloudinary
    @Autowired
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    // Método para subir una imagen a Cloudinary
    public Map upload(MultipartFile multipartFile) throws IOException {
        try {
            // Convertimos el MultipartFile a File
            File file = convert(multipartFile);
            // Subimos el archivo a Cloudinary y obtenemos el resultado
            Map result = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
            // Eliminamos el archivo temporal después de subirlo
            file.delete();
            return result; // Devolvemos el resultado de la subida
        } catch (IOException e) {
            // Lanzamos una excepción si ocurre un error
            throw new IOException("Error al subir la imagen", e);
        }
    }

    // Método para eliminar una imagen de Cloudinary usando su ID
    public Map delete(String id) throws IOException {
        try {
            // Eliminamos la imagen de Cloudinary y obtenemos el resultado
            return cloudinary.uploader().destroy(id, ObjectUtils.emptyMap());
        } catch (IOException e) {
            // Lanzamos una excepción si ocurre un error
            throw new IOException("Error al eliminar la imagen", e);
        }
    }

    // Método privado para convertir un MultipartFile a File
    private File convert(MultipartFile multipartFile) throws IOException {
        // Creamos un archivo temporal con el nombre original del archivo
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        // Escribimos los bytes del MultipartFile al archivo temporal
        FileOutputStream fo = new FileOutputStream(file);
        fo.write(multipartFile.getBytes());
        fo.close();
        return file; // Devolvemos el archivo temporal
    }
}
