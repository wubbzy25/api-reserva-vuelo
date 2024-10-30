package com.api.reservavuelos.Utils;

//importamos las librerias necesarias
import org.springframework.stereotype.Component;

import java.util.Random;


//definimos la clase GenerateCodes y le colocamos @component para que spring lo reconozca
@Component
public class GenerateCodes {
    //definimos el metodo que generar los codigos de manera random
    public int code(){
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return code;
    }
}
