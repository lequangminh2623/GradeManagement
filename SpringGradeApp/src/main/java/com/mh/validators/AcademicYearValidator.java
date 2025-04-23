package com.mh.validators;

import com.mh.pojo.AcademicYear;
import com.mh.services.AcademicYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author leoma
 */
@Component
public class AcademicYearValidator implements Validator {

    @Autowired
    private AcademicYearService academicYearService;

    @Override
    public boolean supports(Class<?> clazz) {
        return AcademicYear.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AcademicYear year = (AcademicYear) target;

        if (year.getYear() == null || year.getYear().trim().isEmpty()) {
            errors.rejectValue("year", "academicYear.year.notNull");
        }

        if (!errors.hasFieldErrors("year")) {
            boolean existYearByYear = academicYearService.existAcademicYearByYear(year.getYear(), year.getId());

            if (existYearByYear) {
                errors.rejectValue("year", "academicYear.year.unique");
            }
        }
    }
}
