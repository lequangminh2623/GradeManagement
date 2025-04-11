/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.services.Impl;

import com.mh.pojo.Semester;
import com.mh.repositories.SemesterRepository;
import com.mh.services.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
/**
 *
 * @author Le Quang Minh
 */

@Service
public class SemesterServiceImpl implements SemesterService {
    
    @Autowired
    private SemesterRepository semesterRepo;

    @Override
    public List<Semester> getSemestersByAcademicYearName(String year) {
        return semesterRepo.getSemestersByAcademicYearName(year);
    }

    @Override
    public List<Semester> getSemesters(Map<String, String> params) {
        return this.semesterRepo.getSemesters(params);
    }
}