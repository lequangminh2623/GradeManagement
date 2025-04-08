/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.formatters;

import com.mh.pojo.AcademicYear;
import java.text.ParseException;
import java.util.Locale;
import org.springframework.format.Formatter;

/**
 *
 * @author Le Quang Minh
 */
public class AcademicYearFormatter implements Formatter<AcademicYear>{

    @Override
    public String print(AcademicYear academicYear, Locale locale) {
        return String.valueOf(academicYear.getId());
    }

    @Override
    public AcademicYear parse(String academicYearId, Locale locale) throws ParseException {
        AcademicYear ay = new AcademicYear();
        ay.setId(Integer.valueOf(academicYearId));
        
        return ay;
    }
    
}
