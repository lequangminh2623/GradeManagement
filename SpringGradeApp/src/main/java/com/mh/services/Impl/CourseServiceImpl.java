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
public class CourseServiceImpl implements CourseService{
    
    @Autowired
    CourseRepository courseRepo;

    @Override
    public List<Course> getCourses(Map<String, String> params) {
        return this.courseRepo.getCourses(params);
    }

}
