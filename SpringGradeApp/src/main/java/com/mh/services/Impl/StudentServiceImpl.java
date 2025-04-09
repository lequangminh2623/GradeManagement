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

/**
 *
 * @author Le Quang Minh
 */

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepo;

    @Override
    public List<Student> getStudentByUsername(String username) {
        return studentRepo.getStudentByUsername(username);
    }

    @Override
    public Student saveStudent(Student student) {
        return studentRepo.saveStudent(student);
    }

    @Override
    public void deleteStudentByUserId(int userId) {
        this.studentRepo.deleteStudentByUserId(userId);
    }
}
