package com.api.reservavuelos.Controllers;
import com.api.reservavuelos.DTO.Request.*;
import com.api.reservavuelos.DTO.Response.AuthResponseDTO;
import com.api.reservavuelos.DTO.Response.ResponseDTO;
import com.api.reservavuelos.Models.Usuarios;
import com.api.reservavuelos.Services.AuthService;
import com.api.reservavuelos.Utils.DateFormatter;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.hc.client5.http.auth.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {

  private final AuthService authService;
  private final DateFormatter dateFormatter;
  @Autowired
  public AuthController(AuthService authService, DateFormatter dateFormatter){
    this.authService = authService;
    this.dateFormatter = dateFormatter;
  }
    @PostMapping("login")
    public ResponseEntity<AuthResponseDTO> IniciarSesion(@Valid @RequestBody LoginRequestDTO loginRequestDto, HttpServletRequest request) throws InvalidCredentialsException {
      return new ResponseEntity<>(authService.login(loginRequestDto,request), HttpStatus.OK);
    }

    @PostMapping("register")
    public ResponseEntity<AuthResponseDTO> Registrar(@Valid @RequestBody RegisterRequestDTO registerRequestDTO, HttpServletRequest request){
      Usuarios usuario = authService.registrarUsuario(registerRequestDTO);
        return new ResponseEntity<>(new AuthResponseDTO(dateFormatter.formatearFecha(), "P-201", usuario.getId(), "Usuario creado correctamente", request.getRequestURI()), HttpStatus.CREATED);
    }


    @GetMapping("forgot-password")
    public ResponseEntity<ResponseDTO> OlvidarContraseña(@Valid @RequestBody ForgotPasswordRequestDTO forgotPasswordRequestDto, HttpServletRequest request){
    return new ResponseEntity<>(authService.OlvidarContraseña(forgotPasswordRequestDto, request), HttpStatus.OK);
    }

    @PostMapping("verify-code")
  public ResponseEntity<ResponseDTO> VerificarCodigo(@Valid @RequestBody CodigoRequestDTO codigoRequestDTO, HttpServletRequest request){
      return new ResponseEntity<>(authService.VerificarCodigo(codigoRequestDTO,request), HttpStatus.OK);
    }

    @PostMapping("change-password")
    public ResponseEntity<ResponseDTO> CambiarContraseña(@Valid @RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO, HttpServletRequest request){
      return new ResponseEntity<>(authService.CambiarContraseña(resetPasswordRequestDTO, request), HttpStatus.OK);
    }
    @GetMapping("/2FA/setup")
   public ResponseEntity<ResponseDTO> setup2FA(@RequestParam Long id_usuario, HttpServletRequest request) throws Exception {
      return new ResponseEntity<>(authService.TotpSetup(id_usuario,request), HttpStatus.OK);
    }
    @PostMapping("/2FA/verify")
    public ResponseEntity<AuthResponseDTO> verify2FA(@RequestParam Long id_usuario, @Valid @RequestBody Codigo2FARequestDTO codigo2FARequestDTO, HttpServletRequest request) throws JsonProcessingException {
      return new ResponseEntity<>(authService.TotpVerification(id_usuario, codigo2FARequestDTO, request), HttpStatus.OK);
    }
}

