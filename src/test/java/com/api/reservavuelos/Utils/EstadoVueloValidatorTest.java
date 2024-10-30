package com.api.reservavuelos.Utils;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static com.api.reservavuelos.Utils.VueloEstado.EN_VUELO;
import static org.junit.jupiter.api.Assertions.*;

class EstadoVueloValidatorTest {
    private VueloEstado INVALIDO;
    private EstadoVueloValidator validator;

    @BeforeEach
    void setUp() {
        validator = new EstadoVueloValidator();
    }

    @Test
    void testValidVueloEstado() {
        // Suponiendo que VueloEstado tiene un valor 'EN_VUELO'
        boolean result = validator.isValid(EN_VUELO, Mockito.mock(ConstraintValidatorContext.class));
        assertTrue(result, "El estado 'EN_VUELO' debería ser válido.");
    }

    @Test
    void testInvalidVueloEstado() {
        // Suponiendo que hay un estado no válido, por ejemplo 'INVALIDO'
        boolean result = validator.isValid(INVALIDO, Mockito.mock(ConstraintValidatorContext.class));
        assertFalse(result, "El estado 'INVALIDO' debería ser inválido.");
    }

    @Test
    void testNullVueloEstado() {
        boolean result = validator.isValid(null, Mockito.mock(ConstraintValidatorContext.class));
        assertFalse(result, "El estado 'null' debería ser inválido.");
    }
}