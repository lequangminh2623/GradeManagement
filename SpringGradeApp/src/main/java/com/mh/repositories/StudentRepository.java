/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mh.repositories;

import com.mh.pojo.Student;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Le Quang Minh
 */
public interface StudentRepository {
    List<Student> getStudents(Map<String, String> params);
    
    Student getStudentByUserId(int userId);
}
