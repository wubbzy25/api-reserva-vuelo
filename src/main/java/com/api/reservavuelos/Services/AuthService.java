package com.api.reservavuelos.Services;
//importamos las librerias necesarias
import com.api.reservavuelos.DTO.Cache.AuthenticationCacheDTO;
import com.api.reservavuelos.DTO.Cache.ProfileCacheDTO;
import com.api.reservavuelos.DTO.Request.*;
import com.api.reservavuelos.DTO.Response.AuthResponseDTO;
import com.api.reservavuelos.DTO.Response.ResponseDTO;
import com.api.reservavuelos.Exceptions.*;
import com.api.reservavuelos.Models.*;
import com.api.reservavuelos.Repositories.*;
import com.api.reservavuelos.Security.JwtTokenProvider;
import com.api.reservavuelos.Utils.DateFormatter;
import com.api.reservavuelos.Utils.QRCodeGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.hc.client5.http.auth.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


//definimos la clase AuthService y la anotamos con @Service para que Spring la reconozca como un servicio
@Service
public class AuthService {

    //declaramos los repositorios, servicios, variables, etc.
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final CredencialesRepository credencialesRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final EmailSenderService emailSenderService;
    private final ResetPasswordService resetPasswordService;
    private final ProfileImageRepository profileImageRepository;
    private final DateFormatter dateFormatter;
    private final GoogleAuthenticatorService googleAuthenticatorService;
    private final TwoFactorAuthRepository twoFactorAuthRepository;
    private final QRCodeGenerator qrCodeGenerator;
    private final RedisTemplate<String, Object> redisTemplate;

    //aplicamos inyeccion de dependencias por el constructor
    @Autowired
    public AuthService(UsuarioRepository usuarioRepository,
                       RolRepository rolRepository,
                       CredencialesRepository credencialesRepository,
                       JwtTokenProvider jwtTokenProvider,
                       AuthenticationManager authenticationManager,
                       PasswordEncoder passwordEncoder,
                       EmailSenderService emailSenderService,
                       ResetPasswordService resetPasswordService,
                       ProfileImageRepository profileImageRepository,
                       DateFormatter dateFormatter,
                       GoogleAuthenticatorService googleAuthenticatorService,
                       TwoFactorAuthRepository twoFactorAuthRepository,
                       QRCodeGenerator qrCodeGenerator,
                       RedisTemplate<String, Object> redisTemplate) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.credencialesRepository = credencialesRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.emailSenderService = emailSenderService;
        this.resetPasswordService = resetPasswordService;
        this.profileImageRepository = profileImageRepository;
        this.dateFormatter = dateFormatter;
        this.googleAuthenticatorService = googleAuthenticatorService;
        this.twoFactorAuthRepository = twoFactorAuthRepository;
        this.qrCodeGenerator = qrCodeGenerator;
        this.redisTemplate = redisTemplate;
    }


     //metodo para registrar usuarios
    public Usuarios registrarUsuario(RegisterRequestDTO registerRequestDTO){
       //validamos que el email no este registrado
        if (usuarioRepository.existsByEmail(registerRequestDTO.getEmail())){
           throw new UserAlreadyRegisterException();
       }
        //obtenemos el rol usuario
        Roles roles = rolRepository.findByNombre("usuario").orElseThrow(() -> new NoSuchElementException("No se encontro el rol usuario"));
      //creamos un usuario y le asignamos los datos
        Usuarios usuario = new Usuarios();
       usuario.setPrimer_nombre(registerRequestDTO.getPrimer_nombre());
       usuario.setSegundo_nombre(registerRequestDTO.getSegundo_nombre());
       usuario.setPrimer_apellido(registerRequestDTO.getPrimer_apellido());
       usuario.setSegundo_apellido(registerRequestDTO.getSegundo_apellido());
       usuario.setEmail(registerRequestDTO.getEmail());
       usuario.setTelefono(registerRequestDTO.getTelefono());
       usuario.setFecha_nacimiento(registerRequestDTO.getFecha_nacimiento());
       usuario.setGenero(registerRequestDTO.getGenero());
       //creamos una imagen por defecto y le asignamos el usuario
        Profile_image profileImage = new Profile_image();
        profileImage.setImage_url("https://t4.ftcdn.net/jpg/00/64/67/63/360_F_64676383_LdbmhiNM6Ypzb3FM4PPuFP9rHe7ri8Ju.jpg");
        profileImage.setUsuarios(usuario);
        profileImageRepository.save(profileImage);
        //creamos las credenciales, le asignamos la contraseña, la hasheamos y le asignamos el usuario
       Credenciales credencial = new Credenciales();
       credencial.setContraseña(passwordEncoder.encode(registerRequestDTO.getContraseña()));
       credencial.setUsuarios(usuario);
       credencialesRepository.save(credencial);
       usuario.setRoles(Collections.singletonList(roles));
       //guardamos el usuario
       Usuarios usuarioGuardado = usuarioRepository.save(usuario);

        return usuarioGuardado;
    }

    //metodo para iniciar sesion
    public AuthResponseDTO login(LoginRequestDTO dtoLogin, HttpServletRequest request) throws InvalidCredentialsException {
               //validamos que el usuario exista
               Optional<Usuarios> usuarioOptional = usuarioRepository.findByEmail(dtoLogin.getEmail());
                if (usuarioOptional.isEmpty()) {
                    throw new UserNotFoundException();
                }
               //obtenemos el usuario
                Usuarios usuario = usuarioOptional.get();
                //obtenemos la credencial del usuario
                Credenciales credenciales = credencialesRepository.getPasswordByEmail(usuario.getEmail());
                //validamos que la contraseña sea correcta
                boolean PasswordMatch = passwordEncoder.matches(dtoLogin.getContraseña(), credenciales.getContraseña());
                if (!PasswordMatch) {
                    throw new InvalidCredentialsException("Contraseña incorrecta");
                }
                //validamos si el usuario tiene activado el 2FA
                Optional<TwoFactorAuth> TwoFactorAuth = twoFactorAuthRepository.findByid_usuario(usuario.getId());
                //validamos que si el usuario es administrador y no tiene activado el 2FA debe activarlos
                 if(TwoFactorAuth.isEmpty() && usuario.getRoles().stream().anyMatch(rol -> rol.getNombre().equals("administrador"))){
                    throw new IllegalStateException("Los administradores deben tener el 2FA Activado para poder iniciar sesion");
                }
                 //si el usuario tiene activado el 2FA, tiene que introducir el codigo de verificacion para iniciar sesion
                if(TwoFactorAuth.isPresent()){
                    AuthenticationCacheDTO authDTO = new AuthenticationCacheDTO();
                    authDTO.setEmail(dtoLogin.getEmail());
                    authDTO.setContraseña(dtoLogin.getContraseña());
                    redisTemplate.opsForValue().set(dtoLogin.getEmail() + "2FA", authDTO, 5, TimeUnit.MINUTES );
                    return new AuthResponseDTO(dateFormatter.formatearFecha(), "P-200", usuario.getId(), "El usuario se ha logeado pero, tiene activado el 2FA, debe introducir el codigo", request.getRequestURI());
        }
                //iniciamos sesion con los datos de la credencial y le asignamos el token
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                dtoLogin.getEmail(), dtoLogin.getContraseña()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        return new AuthResponseDTO(dateFormatter.formatearFecha(), "200", usuario.getId(), token, request.getRequestURI());
    }

    //metodo para recuperar contraseña
        public ResponseDTO OlvidarContraseña(ForgotPasswordRequestDTO forgotPasswordRequestDTO, HttpServletRequest request){
           //validamos que el email exista
            usuarioRepository.findByEmail(forgotPasswordRequestDTO.getEmail()).orElseThrow(UserNotFoundException::new);
           //obtenemos el email asociado del verify code de la cache para verificar si tiene un codigo
            String VerifyCode = resetPasswordService.getData(forgotPasswordRequestDTO.getEmail());
            if(VerifyCode != null) {
                resetPasswordService.deleteData(VerifyCode);
            }
            //obtenemos el codigo random generado
            String code = resetPasswordService.SetResetCode(forgotPasswordRequestDTO.getEmail());
            //lo enviamos al email registrado
            emailSenderService.sendRestPasswordEmail(forgotPasswordRequestDTO.getEmail(),code);
            //retornamos el response
            return setResponseDTO("P-200", "Se ha enviado un codigo de verificacion al correo electronico registrado", request);
        }

    public ResponseDTO VerificarCodigo(CodigoRequestDTO codigoRequestDTO, HttpServletRequest request) {
        // Obtenemos el código almacenado para el email proporcionado
        String code = resetPasswordService.getData(codigoRequestDTO.getEmail());
        if (code == null){
            throw new CodeNotFoundException("No se ha enviado ningun codigo");
        }

        // Comprobamos si el código proporcionado por el usuario coincide con el almacenado
        if (!Objects.equals(codigoRequestDTO.getCodigo(), code)) {
            // Si no coinciden, lanzamos una excepción indicando que el código no es válido
            throw new CodeNotFoundException("El codigo no es valido");
        }

        // Si coinciden, eliminamos el código almacenado
        resetPasswordService.deleteData(codigoRequestDTO.getEmail());

        // Establecemos el estado de verificación del email a "verificado"
        resetPasswordService.setVerifyStatus(codigoRequestDTO.getEmail());

        // Devolvemos un ResponseDTO indicando que la verificación fue exitosa
        return setResponseDTO("P-200", "El codigo de verificacion fue validado correctamente", request);
    }

    public ResponseDTO CambiarContraseña(ResetPasswordRequestDTO resetPasswordRequestDTO, HttpServletRequest request) {
        // Obtenemos el email del objeto resetPasswordRequestDTO
        String email = resetPasswordRequestDTO.getEmail();

        // Verificamos si el usuario existe en la base de datos, si no existe lanzamos una excepción UserNotFoundException
       Usuarios usuario =  usuarioRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);

        // Comprobamos si las contraseñas proporcionadas coinciden, si no, lanzamos una excepción
        if (!Objects.equals(resetPasswordRequestDTO.getPassword(), resetPasswordRequestDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        // Obtenemos el estado de verificación asociado al email
        String VerifyStatus = resetPasswordService.getData(resetPasswordRequestDTO.getEmail());

        // Verificamos si el estado de verificación es nulo o no es "verified", lanzamos una excepción si es así
        if (VerifyStatus == null || !VerifyStatus.equals("verified")) {
            throw new IllegalArgumentException("No tienes permiso para realizar esta accion");
        }

        // Obtenemos las credenciales del usuario usando su email
        Credenciales credenciales = credencialesRepository.getPasswordByEmail(usuario.getEmail());

        // Verificamos si la nueva contraseña coincide con la actual, lanzamos una excepción si es así
        boolean contraseñaDescifrada = passwordEncoder.matches(resetPasswordRequestDTO.getPassword(), credenciales.getContraseña());
        if (contraseñaDescifrada) {
            throw new IllegalArgumentException("La contraseña actual es igual a la nueva");
        }

        // Codificamos la nueva contraseña y la guardamos en la base de datos
        credenciales.setContraseña(passwordEncoder.encode(resetPasswordRequestDTO.getPassword()));
        credencialesRepository.save(credenciales);

        // Eliminamos el estado de verificación asociado al email
        resetPasswordService.deleteData(email);

        // Devolvemos una respuesta indicando que la contraseña ha sido cambiada correctamente
        return setResponseDTO("P-200", "Contraseña cambiada correctamente", request);
    }

    public ResponseDTO TotpSetup(Long id_usuario, HttpServletRequest request) throws IOException, WriterException {
        // Verificamos si el usuario ya tiene configurado el 2FA
        Optional<TwoFactorAuth> twoFactorAuthOptional = twoFactorAuthRepository.findByid_usuario(id_usuario);
        if (twoFactorAuthOptional.isPresent()) {
            // Si ya tiene 2FA configurado, lanzamos una excepción
            throw new IllegalArgumentException("Ya tienes seteado el 2FA");
        }

        // Obtenemos el usuario por su ID
        Optional<Usuarios> usuarioOptional = usuarioRepository.findById(id_usuario);
        if (usuarioOptional.isEmpty()) {
            // Si no existe el usuario, lanzamos una excepción no autorizada
            throw new UserNotFoundException();
        }
        Usuarios usuario = usuarioOptional.get();

        // Creamos una nueva instancia de TwoFactorAuth
        TwoFactorAuth twoFactor = new TwoFactorAuth();

        // Generamos una clave secreta usando el servicio de Google Authenticator
        twoFactor.setSecretKey(googleAuthenticatorService.generateSecretKey());

        // Asociamos el usuario a la instancia de TwoFactorAuth
        twoFactor.setUsuarios(usuario);

        // Guardamos la configuración de 2FA en el repositorio
        twoFactorAuthRepository.save(twoFactor);

        // Generamos la URL del código QR usando el ID de usuario y la clave secreta
        String qrCodeurl = qrCodeGenerator.getQRCodeURL(id_usuario, twoFactor.getSecretKey());
        // Generamos la imagen del código QR en bytes
        byte[] QRcode = qrCodeGenerator.generateQRCodeImage(qrCodeurl);
        

        // Enviamos un email al usuario con la imagen del código QR adjunta
        emailSenderService.sendEmailWithQRCode(usuario.getEmail(), QRcode);

        // Devolvemos un ResponseDTO indicando que el QR para activar el 2FA se ha enviado al email registrado
        return setResponseDTO("P-200", "Se envio el qr para activar el 2AF al email registrado", request);
    }


    public AuthResponseDTO TotpVerification(Long id_usuario, Codigo2FARequestDTO codigo2FARequestDTO, HttpServletRequest request) throws JsonProcessingException {
        Optional<Usuarios> usuarioOptional = usuarioRepository.findById(id_usuario);
        if (usuarioOptional.isEmpty()) {
            // Si no existe el usuario, lanzamos una excepción no autorizada
            throw new UserNotFoundException();
        }

        // Verificamos si el usuario tiene configurado el 2FA
        Optional<TwoFactorAuth> twoFactorAuthOptional = twoFactorAuthRepository.findByid_usuario(id_usuario);
        if (twoFactorAuthOptional.isEmpty()) {
            // Si no tiene 2FA configurado, lanzamos una excepción
            throw new IllegalArgumentException("No tienes activado el 2FA");
        }

        // Obtenemos el usuario por su ID
        Usuarios usuario = usuarioOptional.get();

        // Obtenemos el cache de autenticación usando el email del usuario
        AuthenticationCacheDTO userCache = (AuthenticationCacheDTO) redisTemplate.opsForValue().get(usuario.getEmail() + "2FA");

        // Si no existe cache de autenticación, lanzamos una excepción no autorizada
        if (userCache == null) {
            throw new UnauthorizedException("No estas autorizado para usar esto");
        }

        // Obtenemos la información de 2FA del usuario
        TwoFactorAuth twoFactorAuth = twoFactorAuthOptional.get();

        // Validamos el código 2FA usando la clave secreta y el código proporcionado
        boolean CodeValidate = googleAuthenticatorService.validateCode(twoFactorAuth.getSecretKey(), codigo2FARequestDTO.getCodigo());

        if (CodeValidate) {
            // Si el código es válido, autenticamos al usuario usando el cache de autenticación
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    userCache.getEmail(), userCache.getContraseña()
            ));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generamos un token JWT para el usuario autenticado
            String token = jwtTokenProvider.generateToken(authentication);

            // Eliminamos el cache de 2FA del usuario
            redisTemplate.delete("2FA" + usuario.getEmail());

            // Devolvemos un AuthResponseDTO indicando que el 2FA fue verificado y el token generado
            return new AuthResponseDTO(dateFormatter.formatearFecha(), "P-200", id_usuario, token, request.getRequestURI());
        } else {
            // Si el código no es válido, lanzamos una excepción de código 2FA no válido
            throw new Code2FAException("El codigo de verificacion no es valido");
        }
    }

    // Método privado para crear un ResponseDTO con la fecha, código y mensaje proporcionados
    private ResponseDTO setResponseDTO(String code, String message, HttpServletRequest request) {
        return new ResponseDTO(dateFormatter.formatearFecha(), code, message, request.getRequestURI());
    }



}
