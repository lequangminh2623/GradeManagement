/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.services.Impl;

import com.mh.pojo.Classroom;
import com.mh.pojo.Student;
import com.mh.repositories.ClassroomRepository;
import com.mh.repositories.StudentRepository;
import com.mh.services.ClassroomService;
import com.mh.services.StudentService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Le Quang Minh
 */
@Service
public class ClassroomServiceImpl implements ClassroomService {

    @Autowired
    ClassroomRepository classroomRepo;

    @Autowired
    StudentService studentService;

    @Override
    public Classroom saveClassroom(Classroom classroom) {
        return this.classroomRepo.saveClassroom(classroom);
    }

    @Override
    public Classroom getClassroomById(Integer id) {
        return this.classroomRepo.getClassroomById(id);
    }

    @Override
    public void deleteClassroom(Integer id) {
        this.classroomRepo.deleteClassroom(id);
    }

    @Override
    public List<Classroom> getClassrooms(Map<String, String> params) {
        return this.classroomRepo.getClassrooms(params);
    }

    @Override
    public Classroom getClassroomWithStudents(Integer id) {
        return this.classroomRepo.getClassroomWithStudents(id);
    }

    @Override
    @Transactional
    public void removeStudentFromClass(int classId, int studentId) {
        Classroom classroom = this.getClassroomById(classId);
        Student student = studentService.getStudentByUserId(studentId);

        classroom.getStudentSet().remove(student);
        student.getClassroomSet().remove(classroom);

        this.saveClassroom(classroom);
    }
}
