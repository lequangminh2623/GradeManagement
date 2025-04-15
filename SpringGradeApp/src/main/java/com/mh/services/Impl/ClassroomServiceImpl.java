/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.services.Impl;

import com.mh.pojo.Classroom;
import com.mh.repositories.ClassroomRepository;
import com.mh.services.ClassroomService;
import com.mh.services.StudentService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Le Quang Minh
 */
@Service
public class ClassroomServiceImpl implements ClassroomService {

    @Autowired
    ClassroomRepository classroomRepo;

    @Override
    public Classroom saveClassroom(Classroom classroom, List<Integer> studentIds) {
        return this.classroomRepo.saveClassroom(classroom, studentIds);
    }

    @Override
    public Classroom getClassroomById(Integer id) {
        return this.classroomRepo.getClassroomById(id);
    }

    @Override
    public void deleteClassroom(Integer id) {
        if (!classroomRepo.getClassroomWithStudents(id).getStudentSet().isEmpty()) {
            throw new IllegalStateException("Cannot delete classroom with enrolled students.");
        }
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
    public int countClassroom(Map<String, String> params) {
        return this.classroomRepo.countClassroom(params);
    }

    @Override
    public void removeStudentFromClassroom(int classroomId, int studentId) {
        this.classroomRepo.removeStudentFromClassroom(classroomId, studentId);
    }
}
