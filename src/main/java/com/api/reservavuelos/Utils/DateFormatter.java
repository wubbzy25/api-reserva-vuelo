package com.api.reservavuelos.Utils;

//importamos las librerias necesarias
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

//definimos la clase DateFormatter y le colocamos @component para que spring lo reconozca
@Component
public class DateFormatter {
    public String formatearFecha(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }
}
