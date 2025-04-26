/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.services.Impl;

import com.mh.pojo.Course;
import com.mh.repositories.CourseRepository;
import com.mh.services.CourseService;
import com.nimbusds.jose.crypto.impl.AAD;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Le Quang Minh
 */
@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository coursesRepotitory;

    @Override
    public List<Course> getCourses(Map<String, String> params) {
        return this.coursesRepotitory.getCourses(params);
    }

    @Override
    public Course saveCourse(Course course) {
        return this.coursesRepotitory.saveCourse(course);
    }

    @Override
    public Course getCourseById(int id) {
        return this.coursesRepotitory.getCourseById(id);
    }

    @Override
    public void deleteCourseById(int id) {
        this.coursesRepotitory.deleteCourseById(id);
    }

    @Override
    public int countCourse(Map<String, String> params) {
        return this.coursesRepotitory.countCourse(params);
    }

    @Override
    public boolean existCourseByName(String name, Integer excludeId) {
        return this.coursesRepotitory.existCourseByName(name, excludeId);
    }

}
