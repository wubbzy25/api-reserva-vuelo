package com.api.reservavuelos;

import com.api.reservavuelos.DTO.Cache.AuthenticationCacheDTO;
import com.api.reservavuelos.DTO.Cache.VueloCacheDTO;
import com.api.reservavuelos.DTO.Request.*;
import com.api.reservavuelos.DTO.Response.VuelosResponseDTO;
import com.api.reservavuelos.Models.*;
import com.api.reservavuelos.Utils.VueloEstado;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.mockito.Mockito.mock;

public class DataProvider {

    public static RegisterRequestDTO obtenerRegisterRequestDTO() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse("2005-08-31");
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO();
        registerRequestDTO.setPrimer_nombre("DanielA");
        registerRequestDTO.setSegundo_nombre("Jose");
        registerRequestDTO.setPrimer_apellido("Gomez");
        registerRequestDTO.setSegundo_apellido("Gonzalez");
        registerRequestDTO.setEmail("test.gmail.com");
        registerRequestDTO.setTelefono("312456789");
        registerRequestDTO.setFecha_nacimiento(date);
        registerRequestDTO.setGenero("Masculino");
        registerRequestDTO.setContraseña("123");    
        return registerRequestDTO;
    }

    public static ForgotPasswordRequestDTO obtenerForgotPasswordRequestDTO(){
        ForgotPasswordRequestDTO forgotPasswordRequestDTO = new ForgotPasswordRequestDTO();
        forgotPasswordRequestDTO.setEmail("test.gmail.com");
        return forgotPasswordRequestDTO;
    }
    public static Profile_image obtenerProfileImagePorDefecto(){
        Profile_image profileImage = new Profile_image();
        profileImage.setId_profile_image(1L);
        profileImage.setImage_url("https://t4.ftcdn.net/jpg/00/64/67/63/360_F_64676383_LdbmhiNM6Ypzb3FM4PPuFP9rHe7ri8Ju.jpg");
        return profileImage;
    }

    public static Roles obtenerRolPorDefecto(){
        Roles roles = new Roles();
        roles.setId_rol(1L);
        roles.setNombre("usuario");
        return roles;
    }

    public static LoginRequestDTO obtenerLoginRequestDTO(){
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setEmail("test.gmail.com");
        loginRequestDTO.setContraseña("123");
        return loginRequestDTO;
    }

    public static Usuarios obtenerUsuarioPorDefectoRolUsuario() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse("2005-08-31");
        Usuarios usuarios = new Usuarios();
        usuarios.setId(1L);
        usuarios.setPrimer_nombre("Daniel");
        usuarios.setSegundo_nombre("Jose");
        usuarios.setPrimer_apellido("Gomez");
        usuarios.setSegundo_apellido("Gonzalez");
        usuarios.setEmail("test.gmail.com");
        usuarios.setTelefono("312456789");


        Profile_image profileImage = new Profile_image();
        profileImage.setId_profile_image(1L);
        profileImage.setUsuarios(usuarios);
        profileImage.setImage_url("https://t4.ftcdn.net/jpg/00/64/67/63/360_F_64676383_LdbmhiNM6Ypzb3FM4PPuFP9rHe7ri8Ju.jpg");
        usuarios.setProfile_image(profileImage);
        Roles roles = new Roles();
        roles.setId_rol(1L);
        roles.setNombre("usuario");

        List<Roles> rolesList = new ArrayList<>();
        rolesList.add(roles);
        usuarios.setRoles(rolesList);

        usuarios.setFecha_nacimiento(date);
        usuarios.setGenero("Masculino");
        return usuarios;
    }

    public static Usuarios obtenerUsuarioPorDefectoRolAdministrador() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse("2004-02-10");
        Usuarios usuarios = new Usuarios();
        usuarios.setId(1L);
        usuarios.setPrimer_nombre("Carlos");
        usuarios.setSegundo_nombre("Andres");
        usuarios.setPrimer_apellido("Salas");
        usuarios.setSegundo_apellido("Correa");
        usuarios.setEmail("testAdmin.gmail.com");
        usuarios.setTelefono("312456729");


        Profile_image profileImage = new Profile_image();
        profileImage.setId_profile_image(1L);
        profileImage.setUsuarios(usuarios);
        profileImage.setImage_url("https://t4.ftcdn.net/jpg/00/64/67/63/360_F_64676383_LdbmhiNM6Ypzb3FM4PPuFP9rHe7ri8Ju.jpg");
        usuarios.setProfile_image(profileImage);
        Roles roles = new Roles();
        roles.setId_rol(1L);
        roles.setNombre("administrador");

        List<Roles> rolesList = new ArrayList<>();
        rolesList.add(roles);
        usuarios.setRoles(rolesList);

        usuarios.setFecha_nacimiento(date);
        usuarios.setGenero("Masculino");
        return usuarios;
    }

    public static Credenciales obtenerCredencialesPorDefecto(){
        Credenciales credenciales = new Credenciales();
        credenciales.setId_credencial(1L);
        credenciales.setContraseña("contraseñaEncriptada");
        return credenciales;
    }


    public static AuthenticationCacheDTO authenticationCacheDTO(){
        AuthenticationCacheDTO authenticationCacheDTO = new AuthenticationCacheDTO();
        authenticationCacheDTO.setEmail("test.gmail.com");
        authenticationCacheDTO.setContraseña("contraseñaEncriptada");
        return authenticationCacheDTO;
    }

    public static CodigoRequestDTO obtenerCodigoRequestDTO(){
        CodigoRequestDTO codigoRequestDTO = new CodigoRequestDTO();
        codigoRequestDTO.setEmail("test.gmail.com");
        codigoRequestDTO.setCodigo("123456");
        return codigoRequestDTO;
    }
    public static ResetPasswordRequestDTO obtenerResetPasswordRequestDTO(){
        ResetPasswordRequestDTO resetPasswordRequestDTO = new ResetPasswordRequestDTO();
        resetPasswordRequestDTO.setEmail("test.gmail.com");
        resetPasswordRequestDTO.setPassword("123");
        resetPasswordRequestDTO.setConfirmPassword("123");
        return resetPasswordRequestDTO;
    }

    public static ResetPasswordRequestDTO obtenerResetPasswordConDiferenteContraseñaRequestDTO(){
        ResetPasswordRequestDTO resetPasswordRequestDTO = new ResetPasswordRequestDTO();
        resetPasswordRequestDTO.setEmail("test.gmail.com");
        resetPasswordRequestDTO.setPassword("123");
        resetPasswordRequestDTO.setConfirmPassword("1234");
        return resetPasswordRequestDTO;
    }
    public static TwoFactorAuth obtenerTwoFactorAuthPorDefecto() {
        TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
        twoFactorAuth.setIdTwoFactorAuth(1L);
        twoFactorAuth.setSecretKey("secretkey");
        twoFactorAuth.setUsuarios(mock(Usuarios.class));
        return twoFactorAuth;
    }

    public static byte[] obtenerImagenQR(){
        byte[] imageBytes = new byte[1024];

        return imageBytes;
    }

    public static Codigo2FARequestDTO obtenerCodigo2FARequestDTO() {
        Codigo2FARequestDTO codigo2FARequestDTO = new Codigo2FARequestDTO();
        codigo2FARequestDTO.setCodigo(123456);
        return codigo2FARequestDTO;
    }

    public static MultipartFile obtenerMultipartFile() {
        MultipartFile file = new MultipartFile() {
            @Override
            public String getName() {
                return "file";
            }

            @Override
            public String getOriginalFilename() {
                return "tempFile.jpg"; // return a valid filename
            }

            @Override
            public String getContentType() {
                return "image/jpeg"; // return a valid content type
            }

            @Override
            public boolean isEmpty() {
                return false; // this file is not empty
            }

            @Override
            public long getSize() {
                return 1024; // return a file size
            }

            @Override
            public byte[] getBytes() throws IOException {
                return "dummy content".getBytes(); // return some dummy bytes
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream("dummy content".getBytes()); // return InputStream of the dummy content
            }

            @Override
            public Resource getResource() {
                return null; // or return a mock resource if needed
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
                // Optionally implement transferTo
            }

            @Override
            public void transferTo(Path dest) throws IOException, IllegalStateException {
                // Optionally implement transferTo
            }
        };
        return file;
    }
    public static Map getMap() {
        Map result = new Map() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean containsKey(Object key) {
                return false;
            }

            @Override
            public boolean containsValue(Object value) {
                return false;
            }

            @Override
            public Object get(Object key) {
                return null;
            }

            @Override
            public Object put(Object key, Object value) {
                return null;
            }

            @Override
            public Object remove(Object key) {
                return null;
            }

            @Override
            public void putAll(Map m) {

            }

            @Override
            public void clear() {

            }

            @Override
            public Set keySet() {
                return Set.of();
            }

            @Override
            public Collection values() {
                return List.of();
            }

            @Override
            public Set<Entry> entrySet() {
                return Set.of();
            }
        };
        return result;
    }


    public static GoogleAuthenticatorKey obtenerKey(){
        GoogleAuthenticatorKey key = new GoogleAuthenticatorKey.Builder("a")
                .setKey("key")
                .build();
        return key;
    }

    public static ProfileRequestDTO obtenerProfileRequestDTO() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse("2005-08-31");
        ProfileRequestDTO requestDTO = new ProfileRequestDTO();
        requestDTO.setPrimer_nombre("Carlos");
        requestDTO.setSegundo_nombre("Andres");
        requestDTO.setPrimer_apellido("Salas");
        requestDTO.setSegundo_apellido("Correa");
        requestDTO.setTelefono("312456789");
        requestDTO.setEmail("carlos.andres.salas@gmail.com");
        requestDTO.setGenero("Masculino");
        requestDTO.setFecha_nacimiento(date);
        return requestDTO;
    }

    public static Usuarios obtenerUsuarioActualizado() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse("2005-08-31");
        Usuarios usuario = new Usuarios();
        usuario.setId(1L);
        usuario.setPrimer_nombre("Carlos");
        usuario.setSegundo_nombre("Andres");
        usuario.setPrimer_apellido("Salas");
        usuario.setSegundo_apellido("Correa");
        usuario.setTelefono("312456789");
        usuario.setFecha_nacimiento(date);
        usuario.setEmail("carlos.andres.salas@gmail.com");
        usuario.setGenero("Masculino");
        return usuario;
    }

    public static MultipartFile obtenerMultipartFileConntentWrong(){
        MultipartFile file = new MultipartFile() {
            @Override
            public String getName() {
                return "";
            }

            @Override
            public String getOriginalFilename() {
                return "";
            }

            @Override
            public String getContentType() {
                return "";
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte[] getBytes() {
                return new byte[0];
            }

            @Override
            public InputStream getInputStream() {
                return null;
            }

            @Override
            public void transferTo(File dest) throws IllegalStateException {

            }
        };
        return file;
    }

    public static ReservaRequestDTO obtenerReservaRequestDTO(){
        ReservaRequestDTO requestDTO = new ReservaRequestDTO();
        requestDTO.setId_usuario(1L);
        requestDTO.setClase("bussiness");
        requestDTO.setNumero_asiento(1);
        return requestDTO;

    }

    public static Vuelos obtenerVuelo(){
        Vuelos vuelo = new Vuelos();
        vuelo.setIdVuelo(1L);
        vuelo.setNumeroVuelo(1);
        vuelo.setAerolinea("Avianca");
        vuelo.setOrigen("BOG");
        vuelo.setDestino("MEX");
        vuelo.setEconomyClass(10);
        vuelo.setBussinessClass(5);
        return vuelo;
    }

    public static VuelosResponseDTO obtenerVueloResponseDTO(){
        VuelosResponseDTO responseDTO = new VuelosResponseDTO();
        responseDTO.setIdVuelo(1L);
        responseDTO.setNumeroVuelo(1);
        responseDTO.setAerolinea("Avianca");
        responseDTO.setOrigen("BOG");
        responseDTO.setDestino("MEX");
        responseDTO.setDuracion("6 horas");
        responseDTO.setEstadoVuelo(VueloEstado.valueOf("PROGRAMADO"));
        responseDTO.setTipoAvion("Airbus A320");
        responseDTO.setEconomyClass(10);
        responseDTO.setBussinessClass(5);
        return responseDTO;
    }

    public static Reservas obtenerReserva(){
        Reservas reserva = new Reservas();
        reserva.setIdReserva(1L);
        reserva.setEstado("reservado");
        reserva.setClase("economy");
        return reserva;
    }

    public static List<Vuelos> obtenerVuelos(){
        List<Vuelos> vuelos = new ArrayList<>();
        Vuelos vuelo = new Vuelos();
        vuelo.setIdVuelo(1L);
        vuelo.setNumeroVuelo(1);
        vuelo.setAerolinea("Avianca");
        vuelo.setOrigen("BOG");
        vuelo.setDestino("MEX");
        vuelo.setDuracion("6 horas");
        vuelo.setEstadoVuelo(VueloEstado.valueOf("PROGRAMADO"));
        vuelo.setTipoAvion("Airbus A320");
        vuelo.setEconomyClass(10);
        vuelo.setBussinessClass(5);
        vuelos.add(vuelo);
        return vuelos;
    }
    public static List<VueloCacheDTO> obtenerVuelosCacheDTO(){
        List<VueloCacheDTO> vuelosCacheDTO = new ArrayList<>();
        VueloCacheDTO vueloCacheDTO = new VueloCacheDTO();
        vueloCacheDTO.setIdVuelo(1L);
        vueloCacheDTO.setNumeroVuelo(1);
        vueloCacheDTO.setAerolinea("Avianca");
        vueloCacheDTO.setOrigen("BOG");
        vueloCacheDTO.setDestino("MEX");
        vueloCacheDTO.setDuracion("6 horas");
        vueloCacheDTO.setEstadoVuelo("PROGRAMADO");
        vueloCacheDTO.setTipoAvion("Airbus A320");
        vueloCacheDTO.setEconomyClass(10);
        vueloCacheDTO.setBussinessClass(5);
        vuelosCacheDTO.add(vueloCacheDTO);
       return vuelosCacheDTO;
    }

    public static VuelosRequestDTO obtenerVuelosRequestDTO() {
        VuelosRequestDTO requestDTO = new VuelosRequestDTO();
        requestDTO.setAerolinea("Avianca");
        requestDTO.setNumeroVuelo(1234);
        requestDTO.setTipoAvion("Airbus A320");
        requestDTO.setOrigen("BOG");
        requestDTO.setDestino("MEX");
        requestDTO.setFechaIda(LocalDate.now().plusDays(5));
        requestDTO.setHoraSalida(LocalTime.of(10, 30));
        requestDTO.setFechaVuelta(LocalDate.now().plusDays(10));
        requestDTO.setHoraVuelta(LocalTime.of(14, 45));
        requestDTO.setDuracion("4 horas 15 minutos");
        requestDTO.setBussinessClass(5);
        requestDTO.setEconomyClass(10);
        requestDTO.setPrecioBussiness(new BigDecimal("250.00"));
        requestDTO.setPrecioEconomy(new BigDecimal("100.00"));
        return requestDTO;
    }

    public static VueloUpdateStateRequestDTO obtenerVueloUpdateStateDTO(){
        VueloUpdateStateRequestDTO requestDTO = new VueloUpdateStateRequestDTO();
        requestDTO.setEstado(VueloEstado.valueOf("CANCELADO"));
        return requestDTO;
    }
}
