/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mh.repositories;

import com.mh.pojo.Student;
import java.util.List;

/**
 *
 * @author Le Quang Minh
 */
public interface StudentRepository {
    List<Student> getStudentByUsername(String username);
}
