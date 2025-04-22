/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.formatters;

import com.mh.pojo.Student;
import com.mh.services.StudentService;
import java.text.ParseException;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;
/**
 *
 * @author Le Quang Minh
 */
@Component
public class StudentFormatter implements Formatter<Student> {

    @Autowired
    private StudentService studentService;

    @Override
    public String print(Student student, Locale locale) {
        return String.valueOf(student.getId());
    }

    @Override
    public Student parse(String studentId, Locale locale) throws ParseException {
        try {
            return studentService.getStudentByUserId(Integer.parseInt(studentId));
        } catch (NumberFormatException e) {
            throw new ParseException("Không thể tìm thấy sinh viên với ID: " + studentId, 0);
        }
    }
}

