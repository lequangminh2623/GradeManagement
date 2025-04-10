/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.controllers;

import com.mh.pojo.Course;
import com.mh.services.CourseService;
import com.mh.utils.ExceptionUtils;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author leoma
 */
@Controller
public class CourseController {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private CourseService courseService;

    @GetMapping("/courses")
    public String listCourses(Model model, @RequestParam Map<String, String> params) {
        String page = params.get("page");

        if (page == null || page.isEmpty()) {
            params.put("page", "1");
        }

        List<Course> courses = this.courseService.getCourses(params);
        model.addAttribute("courses", courses);
        model.addAttribute("currentPage", Integer.parseInt(params.get("page")));
        model.addAttribute("totalPages", (int) Math.ceil((double) courses.size() / PAGE_SIZE));
        model.addAttribute("kw", params.get("kw"));
        return "/course/course-list";
    }

    @GetMapping("courses/add")
    public String addCoures(Model model) {
        Course course = new Course();
        model.addAttribute("course", course);
        return "course/course-form";
    }

    @PostMapping("/courses")
    public String saveCourse(@ModelAttribute("course") @Valid Course course, Model model) {
        try {
            this.courseService.saveCourse(course);
            return "redirect:/courses";
        } catch (Exception e) {
            Throwable root = ExceptionUtils.getRootCause(e);
            if (root instanceof java.sql.SQLIntegrityConstraintViolationException && root.getMessage().contains("Duplicate entry")) {
                if (root.getMessage().contains("course.name")) {
                    model.addAttribute("errorMessage", "Tên này đã tồn tại.");
                }
            } else {
                model.addAttribute("errorMessage", "Đã xảy ra lỗi: " + root.getMessage());
            }

            model.addAttribute("course", course);
            return "/course/course-form";
        }
    }

    @GetMapping("/courses/{id}")
    public String updateCourse(Model model, @PathVariable(value = "id") int id) {
        Course course = this.courseService.getCourseById(id);
        model.addAttribute("course", course);
        return "/course/course-form";
    }

    @DeleteMapping("courses/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourse(@PathVariable(value = "id") int id) {
        this.courseService.deleteCourseById(id);
    }

}
