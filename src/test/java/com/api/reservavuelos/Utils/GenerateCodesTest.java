package com.api.reservavuelos.Utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;


class GenerateCodesTest {

    GenerateCodes generateCodes;

    @DisplayName("Cuando se llama al metodo code() el metodo retorna un entero con 6 digitos aleatorios")
    @Test
    void codeReturnsSixDigitNumber() {
        generateCodes = new GenerateCodes();
        int result = generateCodes.code();
        assertTrue(result >= 100000 && result <= 999999);
    }
}