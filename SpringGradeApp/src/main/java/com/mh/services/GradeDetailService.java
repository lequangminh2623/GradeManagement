/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mh.services;

import com.mh.pojo.Classroom;
import com.mh.pojo.GradeDetail;
import com.mh.pojo.Student;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Le Quang Minh
 */
public interface GradeDetailService {
    
    List<GradeDetail> getGradeDetail(Map<String, Integer> params);
    
    void deleteGradeDetail(Integer id);
    
    GradeDetail saveGradeDetail(GradeDetail gd);
    
    void saveGradesForStudent(Student student, Classroom savedClassroom, Map<String, String> allParams);
    
    boolean existsByStudentAndCourseAndSemester(Integer studentId, Integer courseId, Integer semesterId, Integer excludeId);
}
