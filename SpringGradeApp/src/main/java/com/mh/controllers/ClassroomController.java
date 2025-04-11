/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.controllers;

import com.mh.pojo.Student;
import com.mh.pojo.Classroom;
import com.mh.services.StudentService;
import com.mh.services.ClassroomService;
import com.mh.services.CourseService;
import com.mh.services.SemesterService;
import com.mh.services.UserService;
import com.mh.utils.PageSize;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author Le Quang Minh
 */
@Controller
@RequestMapping("/classrooms")
public class ClassroomController {

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private StudentService studentService;
    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @Autowired
    private SemesterService semesterService;

    @GetMapping("")
    public String listClassrooms(Model model, @RequestParam Map<String, String> params) {
        String page = params.get("page");

        if (page == null || page.isEmpty()) {
            params.put("page", "1");
        }
        
        List<Classroom> classrooms = this.classroomService.getClassrooms(params);
        model.addAttribute("classrooms", classrooms);
        model.addAttribute("currentPage", Integer.parseInt(params.get("page")));
        model.addAttribute("totalPages", (int) Math.ceil((double) this.classroomService.countClassroom(params) / PageSize.CLASSROOM_PAGE_SIZE.getSize()));
        model.addAttribute("kw", params.get("kw"));
        return "/classroom/classroom-list";
    }

    @GetMapping("/add")
    public String addClassroom(Model model, Map<String, String> params) {
        Map<String, String> role = new HashMap<>();
        role.put("role", "ROLE_LECTURER");

        model.addAttribute("classroom", new Classroom());
        model.addAttribute("students", studentService.getStudents(null));
        model.addAttribute("courses", courseService.getCourses(null));
        model.addAttribute("semesters", semesterService.getSemesters(null));
        model.addAttribute("lecturers", userService.getUsers(role));

        return "/classroom/classroom-form";
    }

    @GetMapping("/{id}")
    public String updateClassroom(@PathVariable("id") Integer id, Model model, Map<String, String> params) {
        Map<String, String> role = new HashMap<>();
        role.put("role", "ROLE_LECTURER");

        Classroom classroom = classroomService.getClassroomWithStudents(id);
        if (classroom.getStudentSet() == null) {
            classroom.setStudentSet(new HashSet<>());
        }

        model.addAttribute("classroom", classroom);
        model.addAttribute("students", studentService.getStudents(null));
        model.addAttribute("courses", courseService.getCourses(null));
        model.addAttribute("semesters", semesterService.getSemesters(null));
        model.addAttribute("lecturers", userService.getUsers(role));

        return "/classrooms/classroom-form";
    }

    @PostMapping("")
    public String saveClassroom(@ModelAttribute("classroom") Classroom classroom,
            @RequestParam(name = "studentIds", required = false) List<Integer> studentIds) {

        Classroom existing = classroomService.getClassroomWithStudents(classroom.getId());

        if (studentIds != null) {
            Set<Student> selectedStudents = studentIds.stream()
                    .map(studentService::getStudentByUserId)
                    .collect(Collectors.toSet());
            existing.getStudentSet().addAll(selectedStudents);
        }

        classroomService.saveClassroom(existing);
        return "redirect:/classrooms";
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClassroom(@PathVariable("id") Integer id) {
        classroomService.deleteClassroom(id);
    }

    @DeleteMapping("/{classId}/students/{studentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeStudentFromClass(
            @PathVariable("classId") Integer classId,
            @PathVariable("studentId") Integer studentId) {
        classroomService.removeStudentFromClass(classId, studentId);
    }

}
