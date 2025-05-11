/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.pojo.dto;

import com.mh.pojo.GradeDetail;

/**
 *
 * @author leoma
 */
public class GradeDetailDTO {

    private String classroomName;
    private GradeDetail gradeDetail;

    public GradeDetailDTO() {
    }

    public GradeDetailDTO(GradeDetail gradeDetail, String classroomName) {
        this.gradeDetail = gradeDetail;
        this.classroomName = classroomName;
    }

    /**
     * @return the classroomName
     */
    public String getClassroomName() {
        return classroomName;
    }

    /**
     * @param classroomName the classroomName to set
     */
    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    /**
     * @return the gradeDetail
     */
    public GradeDetail getGradeDetail() {
        return gradeDetail;
    }

    /**
     * @param gradeDetail the gradeDetail to set
     */
    public void setGradeDetail(GradeDetail gradeDetail) {
        this.gradeDetail = gradeDetail;
    }

}
