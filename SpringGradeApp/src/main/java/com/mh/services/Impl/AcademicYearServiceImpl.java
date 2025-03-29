/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.services.Impl;

import com.mh.pojo.AcademicYear;
import com.mh.services.AcademicYearService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Le Quang Minh
 */
@Service
public class AcademicYearServiceImpl implements AcademicYearService{
    @Autowired
    private AcademicYearService academicYearRepo;

    @Override
    public List<AcademicYear> getAcademicYears() {
        return academicYearRepo.getAcademicYears();
    }
    
}
