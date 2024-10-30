package com.api.reservavuelos.Services;

import com.api.reservavuelos.DTO.Cache.AuthenticationCacheDTO;
import com.api.reservavuelos.DTO.Request.LoginRequestDTO;
import com.api.reservavuelos.DTO.Request.RegisterRequestDTO;
import com.api.reservavuelos.DTO.Response.AuthResponseDTO;
import com.api.reservavuelos.DataProvider;
import com.api.reservavuelos.Exceptions.UserAlreadyRegisterException;
import com.api.reservavuelos.Exceptions.UserNotFoundException;
import com.api.reservavuelos.Models.*;
import com.api.reservavuelos.Repositories.*;
import com.api.reservavuelos.Security.JwtTokenProvider;
import com.api.reservavuelos.Utils.DateFormatter;
import com.api.reservavuelos.Utils.QRCodeGenerator;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.hc.client5.http.auth.InvalidCredentialsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.ArgumentUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.xml.crypto.Data;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest   {

    @Mock
    UsuarioRepository usuarioRepository;
    @Mock
    RolRepository rolRepository;
    @Mock
    CredencialesRepository credencialesRepository;
    @Mock
    JwtTokenProvider jwtTokenProvider;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    EmailSenderService emailSenderService;
    @Mock
    ResetPasswordService resetPasswordService;
    @Mock
    ProfileImageRepository profileImageRepository;
    @Mock
    DateFormatter dateFormatter;
    @Mock
    GoogleAuthenticatorService googleAuthenticatorService;
    @Mock
    TwoFactorAuthRepository twoFactorAuthRepository;
    @Mock
    QRCodeGenerator qrCodeGenerator;
    @Mock
    RedisTemplate<String, Object> redisTemplate;
    HttpServletRequest request = mock(HttpServletRequest.class);
    @InjectMocks
    AuthService authService;

    AuthServiceTest() throws ParseException {
    }

    //metodo registrar usuario
    @DisplayName("Si el email ya existe lanza una excepcion")
    @Test
    void LanzarExcepcionSiElEmailYaExiste() throws ParseException {
        RegisterRequestDTO registerRequestDTO = DataProvider.obtenerRegisterRequestDTO();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(usuarioRepository.existsByEmail("test.gmail.com")).thenReturn(true);
        assertThrows(UserAlreadyRegisterException.class, () -> authService.registrarUsuario(registerRequestDTO));
        verify(usuarioRepository, times(1)).existsByEmail("test.gmail.com");
    }

    @DisplayName("Lanzar una excepcion si no se encuentra el rol")
    @Test
    void lanzarExcepcionSiNoSeEncuentraElRol() throws ParseException {
        RegisterRequestDTO registerRequestDTO = DataProvider.obtenerRegisterRequestDTO();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(rolRepository.findByNombre("usuario")).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> authService.registrarUsuario(registerRequestDTO));
        verify(rolRepository, times(1)).findByNombre("usuario");
    }

    @DisplayName("Registrar el usuario si el email no existe")
    @Test
    void testRegistrarUsuario() throws ParseException {
       RegisterRequestDTO registerRequestDTO = DataProvider.obtenerRegisterRequestDTO();

       when(usuarioRepository.existsByEmail("test.gmail.com")).thenReturn(false);
       when(rolRepository.findByNombre("usuario")).thenReturn(Optional.of(DataProvider.obtenerRolPorDefecto()));
        when(profileImageRepository.save(any(Profile_image.class))).thenAnswer(invocation -> {
            Profile_image profileImage = invocation.getArgument(0);
            profileImage.setId_profile_image(1L);
            return profileImage;
        });
       when(passwordEncoder.encode(any())).thenReturn("contraseñaEncriptada");
       when(credencialesRepository.save(any(Credenciales.class))).thenAnswer(invocation -> {
            Credenciales credenciales = invocation.getArgument(0);
            credenciales.setId_credencial(1L);
            credenciales.setContraseña("contraseñaEncriptada");
            return credenciales;
       } );
        when(usuarioRepository.save(any(Usuarios.class))).thenReturn(DataProvider.obtenerUsuarioPorDefectoRolUsuario());

       Usuarios usuario = authService.registrarUsuario(registerRequestDTO);

       //verificamos que le usuario tenga los roles correctamente
       assertNotNull(usuario.getRoles());
       assertEquals(1, usuario.getRoles().size());
       assertEquals("usuario", usuario.getRoles().get(0).getNombre());

       //verificamos que el usuario tenga los datos correctos
       assertEquals(1L, usuario.getId());
       assertEquals("Daniel", usuario.getPrimer_nombre());
       assertEquals("Jose", usuario.getSegundo_nombre());
       assertEquals("Gomez", usuario.getPrimer_apellido());
       assertEquals("Gonzalez", usuario.getSegundo_apellido());
       assertEquals("test.gmail.com", usuario.getEmail());
       assertEquals("312456789", usuario.getTelefono());
       //verificamos que los metodos se llamen correctamente
       verify(usuarioRepository, times(1)).existsByEmail("test.gmail.com");
       verify(rolRepository, times(1)).findByNombre("usuario");
       verify(profileImageRepository, times(1)).save(any());
       verify(passwordEncoder, times(1)).encode(any());
       verify(credencialesRepository, times(1)).save(any());
       verify(usuarioRepository, times(1)).save(any(Usuarios.class));

        ArgumentCaptor<Credenciales> CredencialesCaptor = ArgumentCaptor.forClass(Credenciales.class);
        verify(credencialesRepository).save(CredencialesCaptor.capture());
        Credenciales credenciales = CredencialesCaptor.getValue();
        //verificar que el usuario tiene credenciales correctamente
        assertEquals(1L, credenciales.getId_credencial());
        assertEquals("contraseñaEncriptada", credenciales.getContraseña());

        //verificamos que el usuario tenga el profile image correcto
        ArgumentCaptor<Profile_image> ProfileImageCaptor = ArgumentCaptor.forClass(Profile_image.class);
        verify(profileImageRepository).save(ProfileImageCaptor.capture());
        Profile_image profile_image = ProfileImageCaptor.getValue();
        assertEquals(1L, profile_image.getId_profile_image());
        assertEquals("https://t4.ftcdn.net/jpg/00/64/67/63/360_F_64676383_LdbmhiNM6Ypzb3FM4PPuFP9rHe7ri8Ju.jpg", profile_image.getImage_url());
    }


    @DisplayName("Si el usuario no existe lanza una excepcion de tipo UserNotFoundException")
    @Test
    void LanzarExcepcionSiNoSeEncuentraElUsuario() {
        LoginRequestDTO loginRequestDTO = DataProvider.obtenerLoginRequestDTO();

        when(usuarioRepository.findByEmail("test.gmail.com")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> authService.login(loginRequestDTO, request));
    }

    @DisplayName("Si la contraseña no es correcta lanza una excepcion de tipo InvalidCredentialsException")
    @Test
    void LanzarExcepcionSiLaContraseñaNoEsCorrecta() throws ParseException {
        // Preparar datos de prueba
        LoginRequestDTO loginRequestDTO = DataProvider.obtenerLoginRequestDTO();

        // Configurar el comportamiento del mock para devolver un usuario cuando se busca por email
        when(usuarioRepository.findByEmail("test.gmail.com")).thenReturn(Optional.of(DataProvider.obtenerUsuarioPorDefectoRolUsuario()));

        // Configurar el mock para obtener credenciales
        when(credencialesRepository.getPasswordByEmail("test.gmail.com")).thenReturn(DataProvider.obtenerCredencialesPorDefecto());

        // Configurar el encoder para simular que la contraseña no coincide
        when(passwordEncoder.matches("123", "contraseñaEncriptada")).thenReturn(false);

        // Ejecutar y verificar la excepción
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            authService.login(loginRequestDTO, request);
        });

        // Asegúrate de que la excepción lanzada es la esperada
        assertEquals("Contraseña incorrecta", exception.getMessage());

        // Verificaciones adicionales
        verify(usuarioRepository).findByEmail("test.gmail.com");
        verify(credencialesRepository).getPasswordByEmail("test.gmail.com");
        verify(passwordEncoder).matches("123", "contraseñaEncriptada");
    }
    @DisplayName("Si el usuario es administrador y no tiene activado el 2FA lanza una excepcion de tipo IllegalStateException")
    @Test
    void LanzarExcepcionSiElUsuarioNoTieneActivadoEl2FAyEsAdminsitrador() throws ParseException {
        when(usuarioRepository.findByEmail("test.gmail.com")).thenReturn(Optional.of(DataProvider.obtenerUsuarioPorDefectoRolAdministrador()));
        when(credencialesRepository.getPasswordByEmail("test.gmail.com")).thenReturn(DataProvider.obtenerCredencialesPorDefecto());
        when(passwordEncoder.matches("123", "contraseñaEncriptada")).thenReturn(true);
        when(twoFactorAuthRepository.findByid_usuario(anyLong())).thenReturn(Optional.empty());

       IllegalStateException exception = assertThrows(IllegalStateException.class, () -> authService.login(DataProvider.obtenerLoginRequestDTO(), request));
       assertEquals("Los administradores deben tener el 2FA Activado para poder iniciar sesion", exception.getMessage());
    }

    @Test
    void LogearUnUsuarioSinA2F() throws ParseException, InvalidCredentialsException {
       when(usuarioRepository.findByEmail("test.gmail.com")).thenReturn(Optional.of(DataProvider.obtenerUsuarioPorDefectoRolUsuario()));
       when(credencialesRepository.getPasswordByEmail("test.gmail.com")).thenReturn(DataProvider.obtenerCredencialesPorDefecto());
       when(passwordEncoder.matches("123", "contraseñaEncriptada")).thenReturn(true);
       when(twoFactorAuthRepository.findByid_usuario(anyLong())).thenReturn(Optional.empty());
       when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("test.gmail.com", "123"))).thenReturn(new UsernamePasswordAuthenticationToken("test.gmail.com", "123"));
       when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("token");
       AuthResponseDTO responseDTO =  authService.login(DataProvider.obtenerLoginRequestDTO(), request);
       assertEquals(responseDTO.getMessage(), "token");

       verify(usuarioRepository,  times(1)).findByEmail("test.gmail.com");
       verify(credencialesRepository, times(1)).getPasswordByEmail("test.gmail.com");
       verify(passwordEncoder, times(1)).matches("123", "contraseñaEncriptada");
       verify(twoFactorAuthRepository, times(1)).findByid_usuario(anyLong());
       verify(authenticationManager, times(1)).authenticate(new UsernamePasswordAuthenticationToken("test.gmail.com", "123"));
       verify(jwtTokenProvider, times(1)).generateToken(any(Authentication.class));
    }

    @Test
    void LogearUnUsuarioConA2F() throws ParseException, InvalidCredentialsException {
        when(usuarioRepository.findByEmail("test.gmail.com")).thenReturn(Optional.of(DataProvider.obtenerUsuarioPorDefectoRolUsuario()));
        when(credencialesRepository.getPasswordByEmail("test.gmail.com")).thenReturn(DataProvider.obtenerCredencialesPorDefecto());
        when(passwordEncoder.matches("123", "contraseñaEncriptada")).thenReturn(true);
        when(twoFactorAuthRepository.findByid_usuario(anyLong())).thenReturn((Optional.of(DataProvider.obtenerTwoFactorAuthPorDefecto())));
        doNothing().when(redisTemplate.opsForValue()).set(
                eq("test.gmail.com" + "2FA"),
                any(AuthenticationCacheDTO.class),
                eq(5L),
                eq(TimeUnit.MINUTES)
        );

        AuthResponseDTO responseDTO = authService.login(DataProvider.obtenerLoginRequestDTO(), request);
    assertEquals("P-200", responseDTO.getCode());
    assertEquals(1L, responseDTO.getIdUsuario());
    assertEquals("El usuario se ha logeado pero, tiene activado el 2FA, debe introducir el codigo", responseDTO.getMessage());

    verify(usuarioRepository, times(1)).findByEmail("test.gmail.com");
    verify(credencialesRepository, times(1)).getPasswordByEmail("test.gmail.com");
    verify(passwordEncoder, times(1)).matches("123", "contraseñaEncriptada");
    verify(twoFactorAuthRepository, times(1)).findByid_usuario(anyLong());
        verify(redisTemplate.opsForValue(), times(1)).set(
                eq("test.gmail.com" + "2FA"),
                any(AuthenticationCacheDTO.class),
                eq(5L),
                eq(TimeUnit.MINUTES)
        );
    }
    @Test
    void olvidarContraseña() {
    }

    @Test
    void verificarCodigo() {
    }

    @Test
    void cambiarContraseña() {
    }

    @Test
    void totpSetup() {
    }

    @Test
    void totpVerification() {
    }
}