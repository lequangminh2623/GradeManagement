/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.controllers;

import com.mh.services.AcademicYearService;
import com.mh.services.ClassroomService;
import com.mh.services.CourseService;
import com.mh.services.ExtraGradeService;
import com.mh.services.ForumPostService;
import com.mh.services.ForumReplyService;
import com.mh.services.GradeDetailService;
import com.mh.services.SemesterService;
import com.mh.services.StudentService;
import com.mh.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Le Quang Minh
 */
@Controller
public class IndexController {
    @Autowired
    AcademicYearService academicYearService;
    @Autowired
    ClassroomService classroomService;
    @Autowired
    CourseService courseService;
    @Autowired
    ExtraGradeService extraGradeService;
    @Autowired
    ForumPostService forumPostService;
    @Autowired
    ForumReplyService forumReplyService;
    @Autowired
    GradeDetailService gradeDetailService;
    @Autowired
    SemesterService semesterService;
    @Autowired
    StudentService studentService;
    @Autowired
    UserService userService;
    
    @RequestMapping("/")
    public String index(Model model) {
//        model.addAttribute("year", this.academicYearService.getAcademicYears());
        return "index";
    }
}
