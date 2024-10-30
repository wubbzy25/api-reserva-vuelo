package com.api.reservavuelos.Security;
import java.time.Duration;

//clase para almacenar las constantes que se usaran en la seguridad
public class ConstantSecurity {
   //tiempo de expiracion del token
    public static final long JWT_EXPIRATION_TOKEN = Duration.parse("P7D").toMillis();
    //firma del token
    public static final String JWT_FIRMA = "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e";
}
