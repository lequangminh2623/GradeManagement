/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.pojo.dto;

import com.mh.validators.ValidExtraGrades;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.util.List;

/**
 *
 * @author Le Quang Minh
 */
public class GradeDTO {

    private Integer studentId;
    private String studentCode;
    private String fullName;
    
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "10.0")
    private Double midtermGrade;
    
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "10.0")
    private Double finalGrade;
    @ValidExtraGrades
    private List<Double> extraGrades;
    
    public GradeDTO() {
        
    }

    public GradeDTO(Integer studentId, Double midtermGrade, Double finalGrade, List<Double> extraGrades) {
        this.studentId = studentId;
        this.midtermGrade = midtermGrade;
        this.finalGrade = finalGrade;
        this.extraGrades = extraGrades;
    }
    
    public GradeDTO(Integer studentId, String studentCode, String fullName, Double midtermGrade, Double finalGrade, List<Double> extraGrades) {
        this.studentId = studentId;
        this.studentCode = studentCode;
        this.fullName = fullName;
        this.midtermGrade = midtermGrade;
        this.finalGrade = finalGrade;
        this.extraGrades = extraGrades;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Double getMidtermGrade() {
        return midtermGrade;
    }

    public void setMidtermGrade(Double midtermGrade) {
        this.midtermGrade = midtermGrade;
    }

    public Double getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(Double finalGrade) {
        this.finalGrade = finalGrade;
    }

    public List<Double> getExtraGrades() {
        return extraGrades;
    }

    public void setExtraGrades(List<Double> extraGrades) {
        this.extraGrades = extraGrades;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
