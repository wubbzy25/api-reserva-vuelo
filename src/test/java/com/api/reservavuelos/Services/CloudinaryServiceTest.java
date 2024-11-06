package com.api.reservavuelos.Services;

import com.api.reservavuelos.DataProvider;
import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudinaryServiceTest {
    @Mock
    Cloudinary cloudinary;


    @Mock
    private Uploader uploader;

     @InjectMocks
    CloudinaryService cloudinaryService;


    @Test
    void lanzarExcepcionCuandoFalleLaSubidaDeUnaImagen() throws IOException {

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(File.class), anyMap())).thenThrow(new IOException("Error al subir la imagen"));

        IOException exception = assertThrows(IOException.class, () -> cloudinaryService.upload(DataProvider.obtenerMultipartFile()));


        assertEquals("Error al subir la imagen", exception.getMessage());

        verify(cloudinary.uploader(), times(1)).upload(any(File.class), anyMap());
    }
    @Test
    void upload() throws IOException {
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(File.class), anyMap())).thenReturn(DataProvider.getMap());

        assertDoesNotThrow(() -> cloudinaryService.upload(DataProvider.obtenerMultipartFile()));

        verify(cloudinary.uploader(), times(1)).upload(any(File.class), anyMap());
    }

    @Test
    void LanzarExcepcionCuandoOcurraUnErrorAlBorrar() throws IOException {
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(anyString(), anyMap())).thenThrow(new IOException("Error al eliminar la imagen"));

        assertThrows(IOException.class, () -> cloudinaryService.delete("id_image"));

        verify(cloudinary.uploader(), times(1)).destroy(anyString(), anyMap());
    }
    @Test
    void delete() throws IOException {
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(anyString(), anyMap())).thenReturn(Map.of("result", "deleted"));

        assertDoesNotThrow(() -> cloudinaryService.delete("id_image"));

        verify(cloudinary.uploader(), times(1)).destroy(anyString(), anyMap());

    }
}