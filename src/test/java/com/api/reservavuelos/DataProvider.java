package com.api.reservavuelos;

import com.api.reservavuelos.DTO.Cache.AuthenticationCacheDTO;
import com.api.reservavuelos.DTO.Request.LoginRequestDTO;
import com.api.reservavuelos.DTO.Request.RegisterRequestDTO;
import com.api.reservavuelos.Models.*;
import jakarta.servlet.http.HttpServletRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public static TwoFactorAuth obtenerTwoFactorAuthPorDefecto(){
        TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
        twoFactorAuth.setIdTwoFactorAuth(1L);
        twoFactorAuth.setSecretKey("secretkey");
        twoFactorAuth.setUsuarios(mock(Usuarios.class));
        return twoFactorAuth;
    }

    public static AuthenticationCacheDTO authenticationCacheDTO(){
        AuthenticationCacheDTO authenticationCacheDTO = new AuthenticationCacheDTO();
        authenticationCacheDTO.setEmail("test.gmail.com");
        authenticationCacheDTO.setContraseña("contraseñaEncriptada");
        return authenticationCacheDTO;
    }
}
