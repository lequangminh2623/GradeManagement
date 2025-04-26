/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.controllers;

import com.mh.pojo.Course;
import com.mh.services.CourseService;
import com.mh.utils.ExceptionUtils;
import com.mh.utils.PageSize;
import com.mh.validators.WebAppValidator;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author leoma
 */
@Controller
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    @Qualifier("webAppValidator")
    private WebAppValidator webAppValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(webAppValidator);
    }

    @GetMapping("/courses")
    public String listCourses(Model model, @RequestParam Map<String, String> params) {
        String page = params.get("page");

        if (page == null || page.isEmpty()) {
            params.put("page", "1");
        }

        List<Course> courses = this.courseService.getCourses(params);
        model.addAttribute("courses", courses);
        model.addAttribute("currentPage", Integer.valueOf(params.get("page")));
        model.addAttribute("totalPages", (int) Math.ceil((double) this.courseService.countCourse(params) / PageSize.COURSE_PAGE_SIZE.getSize()));
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
    public String saveCourse(@ModelAttribute("course") @Valid Course course, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Có lỗi xảy ra");
            return "/course/course-form";
        }

        this.courseService.saveCourse(course);
        return "redirect:/courses";

    }

    @GetMapping("/courses/{id}")
    public String updateCourse(Model model, @PathVariable(value = "id") int id) {
        Course course = this.courseService.getCourseById(id);
        model.addAttribute("course", course);
        return "/course/course-form";
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable("id") int id) {
        try {
            this.courseService.deleteCourseById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Không thể xóa môn học này.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Đã xảy ra lỗi: " + e.getMessage());
        }
    }

}
