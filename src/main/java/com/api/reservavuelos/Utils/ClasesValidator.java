package com.api.reservavuelos.Utils;

//importamos las librerias necesarias
import com.api.reservavuelos.annotations.ValidClase;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

//definimos la clase ClasesValidator que implementa la interfaz ConstraintValidator para poder validar los valores de las clases para que sean las misma de Clases
public class ClasesValidator implements ConstraintValidator<ValidClase, Clases> {
    @Override
    public boolean isValid(Clases clases, ConstraintValidatorContext constraintValidatorContext) {
        if (clases == null){
            return false;
        }
        try {
            Clases.valueOf(clases.name());
            return true;
        } catch (IllegalArgumentException e){
            return false;
        }
    }
}
