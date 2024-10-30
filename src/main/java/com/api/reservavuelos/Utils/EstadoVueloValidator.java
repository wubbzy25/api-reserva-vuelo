package com.api.reservavuelos.Utils;

//importamos las librerias necesarias
import com.api.reservavuelos.annotations.ValidEstadoVuelo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

//definimos la clase EstadoVueloValidator que implementa la interfaz ConstraintValidator para validar si el estado del vuelo coincide ocn algunas de los definimo en VueloEstado
public class EstadoVueloValidator implements ConstraintValidator<ValidEstadoVuelo, VueloEstado> {
    //sobreescribimos el metodo que validara el estado del vuelo
    @Override
    public boolean isValid(VueloEstado vueloEstado, ConstraintValidatorContext constraintValidatorContext) {
        if (vueloEstado == null) {
            return false;
        }
        try {
            VueloEstado.valueOf(vueloEstado.name());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
