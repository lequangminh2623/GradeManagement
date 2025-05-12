/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.pojo.dto;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Le Quang Minh
 */
public class SemesterAnalysisResult {
    private int totalStudents;
    private int weakStudents;
    private double weakRatio;
    private List<String> criticalCourses; // môn có tỷ lệ yếu > 30%
    private Map<String, Double> courseWeakRatios; // course name -> % yếu
    private List<GradeClusterResultDTO> weakStudentList;

    /**
     * @return the totalStudents
     */
    public int getTotalStudents() {
        return totalStudents;
    }

    /**
     * @param totalStudents the totalStudents to set
     */
    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    /**
     * @return the weakStudents
     */
    public int getWeakStudents() {
        return weakStudents;
    }

    /**
     * @param weakStudents the weakStudents to set
     */
    public void setWeakStudents(int weakStudents) {
        this.weakStudents = weakStudents;
    }

    /**
     * @return the weakRatio
     */
    public double getWeakRatio() {
        return weakRatio;
    }

    /**
     * @param weakRatio the weakRatio to set
     */
    public void setWeakRatio(double weakRatio) {
        this.weakRatio = weakRatio;
    }

    /**
     * @return the criticalCourses
     */
    public List<String> getCriticalCourses() {
        return criticalCourses;
    }

    /**
     * @param criticalCourses the criticalCourses to set
     */
    public void setCriticalCourses(List<String> criticalCourses) {
        this.criticalCourses = criticalCourses;
    }

    /**
     * @return the courseWeakRatios
     */
    public Map<String, Double> getCourseWeakRatios() {
        return courseWeakRatios;
    }

    /**
     * @param courseWeakRatios the courseWeakRatios to set
     */
    public void setCourseWeakRatios(Map<String, Double> courseWeakRatios) {
        this.courseWeakRatios = courseWeakRatios;
    }

    /**
     * @return the weakStudentList
     */
    public List<GradeClusterResultDTO> getWeakStudentList() {
        return weakStudentList;
    }

    /**
     * @param weakStudentList the weakStudentList to set
     */
    public void setWeakStudentList(List<GradeClusterResultDTO> weakStudentList) {
        this.weakStudentList = weakStudentList;
    }
    
    
}
