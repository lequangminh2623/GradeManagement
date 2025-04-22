/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.validators;

import com.mh.pojo.GradeDetail;
import com.mh.services.GradeDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * @author Le Quang Minh
 */
@Component
public class GradeValidator implements Validator {

    @Autowired
    private GradeDetailService gradeDetailService;

    @Override
    public boolean supports(Class<?> clazz) {
        return GradeDetail.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        GradeDetail detail = (GradeDetail) target;

        if (detail.getStudent() == null || detail.getStudent().getId() == null) {
            errors.rejectValue("student", "grade.student.notNull");
        }

        if (detail.getCourse() == null || detail.getCourse().getId() == null) {
            errors.rejectValue("course", "grade.course.notNull");
        }

        if (detail.getSemester() == null || detail.getSemester().getId() == null) {
            errors.rejectValue("semester", "grade.semester.notNull");
        }

        if (!errors.hasErrors()) {
            boolean exists = gradeDetailService.existsByStudentAndCourseAndSemester(
                    detail.getStudent().getId(),
                    detail.getCourse().getId(),
                    detail.getSemester().getId(),
                    detail.getId()
            );

            if (exists) {
                errors.reject("grade", "grade.unique");
            }
        }
    }
}
