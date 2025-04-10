/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mh.services;

import com.mh.pojo.Course;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Le Quang Minh
 */
public interface CourseService {

    List<Course> getCourses(Map<String, String> params);

    Course saveCourse(Course course);

    Course getCourseById(int id);

    void deleteCourseById(int id);

}
