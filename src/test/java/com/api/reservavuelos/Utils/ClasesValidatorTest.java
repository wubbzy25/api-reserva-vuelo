package com.api.reservavuelos.Utils;


import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class ClasesValidatorTest {
    Clases invalido;
    ClasesValidator validator;

    @BeforeEach()
    void setUp(){
        validator = new ClasesValidator();
    }
    @DisplayName("Dado una clase que se encuentra en el enum Clases, deberia retornar true")
    @Test
    void validClases(){
        boolean result = validator.isValid(Clases.economy, Mockito.mock(ConstraintValidatorContext.class));
       assertTrue(result, "El estado debe ser valido. (economy o bussiness)");
    }
    @DisplayName("Dado una clase que no se encuentra en el enum Clases, deberia retornar false")
    @Test
    void invalidClases(){
      boolean result = validator.isValid(invalido, Mockito.mock(ConstraintValidatorContext.class));
      assertFalse(result, "El estado debe ser invalido. tiene que ser diferentes a economy o bussiness");
    }

    @Test
    void nullClases(){
        boolean result = validator.isValid(null, Mockito.mock(ConstraintValidatorContext.class));
        assertFalse(result, "El estado debe ser nulo");
    }
}