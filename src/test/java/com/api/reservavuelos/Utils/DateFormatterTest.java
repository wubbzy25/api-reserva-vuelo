package com.api.reservavuelos.Utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
class DateFormatterTest {

    @DisplayName("cuando se llama el metodo formatear la fecha se retorna la fecha en formato String 2024-10-04 12:23:12 ")
    @Test
    void formatearFecha() {
        DateFormatter dateFormatter = new DateFormatter();
        String expectedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String resultado = dateFormatter.formatearFecha();
        assertEquals(expectedDate, resultado);

    }
}