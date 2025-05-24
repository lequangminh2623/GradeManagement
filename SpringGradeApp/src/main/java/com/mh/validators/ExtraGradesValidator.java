package com.mh.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class ExtraGradesValidator implements ConstraintValidator<ValidExtraGrades, List<Double>> {

    @Override
    public boolean isValid(List<Double> extraGrades, ConstraintValidatorContext context) {
        if (extraGrades == null) {
            return true; 
        }
        for (Double grade : extraGrades) {
            if (grade != null && (grade < 0.0 || grade > 10.0)) {
                return false;
            }
        }
        return true;
    }
}
