package com.mh.validators;

import com.mh.pojo.Semester;
import com.mh.services.SemesterService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
public class SemesterValidator implements Validator {

    @Autowired
    private SemesterService semesterService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Semester.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Semester semester = (Semester) target;

        String[] types = {"FIRST_TERM", "SECOND_TERM", "THIRD_TERM"};

        if (semester.getSemesterType() == null || semester.getSemesterType().trim().isEmpty()) {
            errors.rejectValue("semesterType", "semester.semesterType.notNull");

        } else {
            boolean notInArray = Arrays.stream(types).noneMatch(s -> s.equals(semester.getSemesterType()));
            if (notInArray) {
                errors.rejectValue("semesterType", "semester.semesterType.invalid");
            }
        }

        if (!errors.hasFieldErrors()) {
            boolean existSemesterByTypeAndAcademicYearId = semesterService.existSemesterByTypeAndAcademicYearId(semester.getSemesterType(),
                    semester.getId(), semester.getAcademicYear().getId());

            if (existSemesterByTypeAndAcademicYearId) {
                errors.rejectValue("semesterType", "semester.semesterType.unique");
            }
        }
    }
}
