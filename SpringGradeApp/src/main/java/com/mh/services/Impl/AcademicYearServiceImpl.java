/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.services.Impl;

import com.mh.pojo.AcademicYear;
import com.mh.repositories.AcademicYearRepository;
import com.mh.services.AcademicYearService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Le Quang Minh
 */
@Service
public class AcademicYearServiceImpl implements AcademicYearService {

    @Autowired
    private AcademicYearRepository academicYearRepo;

    @Override
    public List<AcademicYear> getAcademicYears(Map<String, String> params) {
        return this.academicYearRepo.getAcademicYears(params);
    }

    @Override
    public int countYears(Map<String, String> params) {
        return this.academicYearRepo.countYears(params);
    }

    @Override
    public AcademicYear saveYear(AcademicYear year) {
        return this.academicYearRepo.saveYear(year);
    }

    @Override
    public AcademicYear getYearById(int id) {
        return this.academicYearRepo.getYearById(id);
    }

    @Override
    public void deleteYearById(int id) {
        this.academicYearRepo.deleteYearById(id);
    }

    @Override
    public boolean existAcademicYearByYear(String year, Integer excludeId) {
        return this.academicYearRepo.existAcademicYearByYear(year, excludeId);
    }

}
