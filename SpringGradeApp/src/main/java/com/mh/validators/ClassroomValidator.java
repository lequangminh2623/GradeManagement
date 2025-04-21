/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.validators;

import com.mh.pojo.Classroom;
import com.mh.services.ClassroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * @author Le Quang Minh
 */
@Component
public class ClassroomValidator implements Validator {

   @Autowired
    private ClassroomService classroomService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Classroom.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Classroom classroom = (Classroom) target;

        if (classroom.getName() == null || 
            classroom.getSemester() == null || 
            classroom.getCourse() == null) {
            return;
        }

        boolean exists = classroomService.existsDuplicateClassroom(
            classroom.getName(),
            classroom.getSemester().getId(),
            classroom.getCourse().getId()
        );

        if (exists) {
            errors.rejectValue("name", "classroom.uniqueErr");
            errors.rejectValue("semester", "classroom.uniqueErr");
            errors.rejectValue("course", "classroom.uniqueErr");
        }
    }
    
}
