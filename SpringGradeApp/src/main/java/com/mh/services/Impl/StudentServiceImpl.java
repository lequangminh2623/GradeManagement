/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.services.Impl;

import com.mh.pojo.Student;
import com.mh.repositories.StudentRepository;
import com.mh.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Le Quang Minh
 */
@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepo;

    @Override
    public List<Student> getStudents(Map<String, String> params) {
        return this.studentRepo.getStudents(params);
    }

    @Override
    public Student getStudentByUserId(int userId) {
        return this.studentRepo.getStudentByUserId(userId);
    }
}
