/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mh.services;

import com.mh.pojo.Student;
import java.util.List;

/**
 *
 * @author Le Quang Minh
 */
public interface StudentService {
    List<Student> getStudentByUsername(String username);
}
