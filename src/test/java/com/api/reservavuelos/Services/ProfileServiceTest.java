package com.api.reservavuelos.Services;

import com.api.reservavuelos.DTO.Cache.ProfileCacheDTO;
import com.api.reservavuelos.DTO.Request.ProfileRequestDTO;
import com.api.reservavuelos.DTO.Response.ResponseDTO;
import com.api.reservavuelos.DataProvider;
import com.api.reservavuelos.Exceptions.UserNotFoundException;
import com.api.reservavuelos.Mappers.ProfileMapper;
import com.api.reservavuelos.Models.Usuarios;
import com.api.reservavuelos.Repositories.ProfileImageRepository;
import com.api.reservavuelos.Repositories.UsuarioRepository;
import com.api.reservavuelos.Security.JwtTokenProvider;
import com.api.reservavuelos.Security.getTokenForRequest;
import com.api.reservavuelos.Utils.DateFormatter;
import com.cloudinary.Api;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.cache.CacheManager;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(PowerMockRunner.class)
class ProfileServiceTest {

    @Mock
    DateFormatter dateFormatter;
    @Mock
    CloudinaryService cloudinaryService;
    @Mock
    UsuarioRepository usuarioRepository;
    @Mock
    JwtTokenProvider jwtTokenProvider;
    @Mock
    getTokenForRequest getTokenForRequest;
    @Mock
    ProfileImageRepository profileImageRepository;
    @Mock
    CacheManager cacheManager;
    @Mock
    RestTemplate restTemplate;
    @Mock
    ObjectMapper objectMapper;
    @Mock
    ProfileMapper profileMapper;
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @InjectMocks
    ProfileService profileService;





    @Test
    void LanzarExcpecionCuandoElUsuarioNoExistaEngetProfile(){
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> profileService.getProfile(1L));

        verify(usuarioRepository, times(1)).findById(anyLong());
    }
    @Test
    void getProfile() throws ParseException {

        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(DataProvider.obtenerUsuarioPorDefectoRolUsuario()));
        when(profileImageRepository.getProfileImage(anyString())).thenReturn(DataProvider.obtenerProfileImagePorDefecto());

        ProfileCacheDTO response = profileService.getProfile(1l);

        assertNotNull(response);
       assertEquals(1L, response.getId_usuario());
        assertEquals("Daniel", response.getPrimer_nombre());
        assertEquals("Jose", response.getSegundo_nombre());
        assertEquals("Gomez", response.getPrimer_apellido());
        assertEquals("Gonzalez", response.getSegundo_apellido());
        assertEquals("test.gmail.com", response.getEmail());
        assertEquals("312456789", response.getTelefono());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(response.getFecha_nacimiento());
        assertEquals("2005-08-31", formattedDate);
        assertEquals("Masculino", response.getGenero());
        assertEquals("Daniel Jose Gomez Gonzalez", response.getNombre_completo());
        assertEquals("https://t4.ftcdn.net/jpg/00/64/67/63/360_F_64676383_LdbmhiNM6Ypzb3FM4PPuFP9rHe7ri8Ju.jpg", response.getUrl_imagen());


        verify(usuarioRepository, times(1)).findById(anyLong());
        verify(profileImageRepository, times(1)).getProfileImage(anyString());
    }

    @Test
    void LanzarExcpecionCuandoElUsuarioNoExistaEnupdateProfile(){
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> profileService.updateProfile(1L, DataProvider.obtenerProfileRequestDTO()));

        verify(usuarioRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateProfile() throws ParseException {
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(DataProvider.obtenerUsuarioPorDefectoRolUsuario()));
        doNothing().when(profileMapper).updateProfileFromDto(any(ProfileRequestDTO.class), any(Usuarios.class));
        when(usuarioRepository.save(any(Usuarios.class))).thenReturn(DataProvider.obtenerUsuarioActualizado());
        when(profileImageRepository.getProfileImage(anyString())).thenReturn(DataProvider.obtenerProfileImagePorDefecto());

        ProfileCacheDTO response = profileService.updateProfile(1L, DataProvider.obtenerProfileRequestDTO());

        assertNotNull(response);
        assertEquals(1L, response.getId_usuario());
        assertEquals("Carlos", response.getPrimer_nombre());
        assertEquals("Andres", response.getSegundo_nombre());
        assertEquals("Salas", response.getPrimer_apellido());
        assertEquals("Correa", response.getSegundo_apellido());
        assertEquals("carlos.andres.salas@gmail.com", response.getEmail());
        assertEquals("312456789", response.getTelefono());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(response.getFecha_nacimiento());
        assertEquals("2005-08-31", formattedDate);
        assertEquals("Masculino", response.getGenero());
        assertEquals("Carlos Andres Salas Correa", response.getNombre_completo());
        assertEquals("https://t4.ftcdn.net/jpg/00/64/67/63/360_F_64676383_LdbmhiNM6Ypzb3FM4PPuFP9rHe7ri8Ju.jpg", response.getUrl_imagen());

        verify(usuarioRepository, times(1)).findById(anyLong());
        verify(profileMapper, times(1)).updateProfileFromDto(any(ProfileRequestDTO.class), any(Usuarios.class));
        verify(usuarioRepository, times(1)).save(any(Usuarios.class));
        verify(profileImageRepository, times(1)).getProfileImage(anyString());
    }


    @Test
    void LanzamosUnaExcepcionSiElTipoDeArchivoNoEsValido() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> profileService.uploadImage(DataProvider.obtenerMultipartFileConntentWrong(), request, response));
       assertEquals("El tipo de archivo no es válido. Sólo se permiten imágenes en formato JPEG o PNG.", exception.getMessage());
    }

    @Test
    void asd() throws Exception {
        String fileHash = "mockedHash";
        String responseBody = "{\"data\": {\"attributes\": {\"last_analysis_stats\": {\"malware\": 0, \"undetected\": 5}}}}}";
        HttpHeaders headers = new HttpHeaders();
        String apiKey = "apikey";
        headers.set("x-apikey", apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(entity), eq(String.class)))
                .thenReturn(ResponseEntity.ok(responseBody));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> profileService.uploadImage(DataProvider.obtenerMultipartFile(), request, response));
        assertEquals("Este archivo puede contener malware", exception.getMessage());
    }

    @Test
    void LanzarUnaExcepcionCuandoElUsuarioNoExistaEnUploadImage() {

        String fileHash = "mockedHash";
        String url = "https://www.virustotal.com/api/v3/files/bf0ecbdb9b814248d086c9b69cf26182d9d4138f2ad3d0637c4555fc8cbf68e5";
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-apikey", "null"); // Use a valid API key for the test
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"data\": {\"attributes\": {\"last_analysis_stats\": {\"malware\": 0, \"undetected\": 5}}}}}", HttpStatus.OK);

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), eq(entity), eq(String.class))).thenReturn(responseEntity);
        // Ensure you are only stubbing what is necessary
        when(getTokenForRequest.getToken(request, response)).thenReturn("token");
        when(jwtTokenProvider.getEmailFromToken(anyString())).thenReturn(null);
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Assert that the exception is thrown
        assertThrows(UserNotFoundException.class, () -> profileService.uploadImage(DataProvider.obtenerMultipartFile(), request, response));

        // Verify interactions
        verify(usuarioRepository, times(1)).findById(anyLong());
        verify(getTokenForRequest, times(1)).getToken(request, response);
        verify(jwtTokenProvider, times(1)).getEmailFromToken(anyString());
    }
    @Test
    void uploadImage() {
    }
}