package com.api.reservavuelos.DTO.Request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class RegisterRequestDTO {
    @NotNull
    @NotEmpty(message = "El nombre es requerido")
    private String primer_nombre;
    private String segundo_nombre;
    @NotNull
    @NotEmpty(message = "El apellido es requerido")
    private String primer_apellido;
    private String segundo_apellido;
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@(gmail\\.com|yahoo\\.com|outlook\\.com|hotmail\\.com|icloud\\.com)$", message = "El correo electronico no es valido")
    @NotEmpty(message =  "El Email es requerido")
    private String email;
    @NotNull
    @NotEmpty(message = "El numero de telefono es requrido")
    @Pattern(regexp = "\\d{10}", message = "El número de teléfono debe tener exactamente 10 dígitos")
    private String telefono;
    @NotNull(message = "la fecha de nacimiento no puede ser null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fecha_nacimiento;
    @NotNull
    @NotEmpty(message = "El género es requerido")
    private String genero;
    @NotEmpty(message = "La contraseña es requerida")
    private String contraseña;
}
