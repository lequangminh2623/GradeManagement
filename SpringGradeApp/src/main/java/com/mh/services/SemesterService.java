/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mh.services;

import com.mh.pojo.Semester;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Le Quang Minh
 */
public interface SemesterService {
    List<Semester> getSemestersByAcademicYearName(String year);
    
    List<Semester> getSemesters(Map<String, String> params);
}
