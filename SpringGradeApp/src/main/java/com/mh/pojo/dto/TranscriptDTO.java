/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.pojo.dto;

import java.util.List;
import jakarta.validation.Valid;

/**
 *
 * @author Le Quang Minh
 */
public class TranscriptDTO {
    
    private String classroomName;
    private String academicTerm;
    private String courseName;
    private String lecturerName;
    private String gradeStatus;
    @Valid
    private List<GradeDTO> grades;

    public String getClassroomName() {
        return classroomName;
    }
    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }
    public String getAcademicTerm() {
        return academicTerm;
    }
    public void setAcademicTerm(String academicTerm) {
        this.academicTerm = academicTerm;
    }
    public String getCourseName() {
        return courseName;
    }
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    public String getLecturerName() {
        return lecturerName;
    }
    public void setLecturerName(String lecturerName) {
        this.lecturerName = lecturerName;
    }
    public List<GradeDTO> getStudents() {
        return getGrades();
    }
    public void setStudents(List<GradeDTO> grades) {
        this.setGrades(grades);
    }

    public String getGradeStatus() {
        return gradeStatus;
    }

    public void setGradeStatus(String gradeStatus) {
        this.gradeStatus = gradeStatus;
    }

    /**
     * @return the grades
     */
    public List<GradeDTO> getGrades() {
        return grades;
    }

    /**
     * @param grades the grades to set
     */
    public void setGrades(List<GradeDTO> grades) {
        this.grades = grades;
    }

}
