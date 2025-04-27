/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.pojo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mh.pojo.Classroom;
import com.mh.pojo.Course;
import com.mh.pojo.Semester;
import com.mh.pojo.User;

/**
 *
 * @author leoma
 */
public class ClassroomDTO {

    private Integer id;
    private String name;
    private String gradeStatus;
    private Course course;
    private Semester semester;
    private User lecturer;

    public ClassroomDTO() {
    }

    public ClassroomDTO(Classroom classroom) {
        this.id = classroom.getId();
        this.name = classroom.getName();
        this.gradeStatus = classroom.getGradeStatus();
        this.course = classroom.getCourse();
        this.semester = classroom.getSemester();
        this.lecturer = classroom.getLecturer();
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the gradeStatus
     */
    public String getGradeStatus() {
        return gradeStatus;
    }

    /**
     * @param gradeStatus the gradeStatus to set
     */
    public void setGradeStatus(String gradeStatus) {
        this.gradeStatus = gradeStatus;
    }

    /**
     * @return the course
     */
    public Course getCourse() {
        return course;
    }

    /**
     * @param course the course to set
     */
    public void setCourse(Course course) {
        this.course = course;
    }

    /**
     * @return the semester
     */
    public Semester getSemester() {
        return semester;
    }

    /**
     * @param semester the semester to set
     */
    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    /**
     * @return the lecturer
     */
    public User getLecturer() {
        return lecturer;
    }

    /**
     * @param lecturer the lecturer to set
     */
    public void setLecturer(User lecturer) {
        this.lecturer = lecturer;
    }

}
