/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mh.services;

import com.mh.pojo.AcademicYear;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Le Quang Minh
 */
public interface AcademicYearService {

    List<AcademicYear> getAcademicYears(Map<String, String> params);

    int countYears(Map<String, String> params);

    AcademicYear saveYear(AcademicYear year);

    AcademicYear getYearById(int id);

    void deleteYearById(int id);

    boolean existAcademicYearByYear(String year, Integer excludeId);

}
