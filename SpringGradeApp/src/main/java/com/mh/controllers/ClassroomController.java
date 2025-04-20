/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.controllers;

import com.mh.pojo.Student;
import com.mh.pojo.Classroom;
import com.mh.pojo.GradeDetail;
import com.mh.services.StudentService;
import com.mh.services.ClassroomService;
import com.mh.services.CourseService;
import com.mh.services.GradeDetailService;
import com.mh.services.SemesterService;
import com.mh.services.UserService;
import com.mh.utils.PageSize;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    private GradeDetailService gradeDetailService;

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
        model.addAttribute("currentPage", Integer.valueOf(params.get("page")));
        model.addAttribute("totalPages", (int) Math.ceil((double) this.classroomService.countClassroom(params) / PageSize.CLASSROOM_PAGE_SIZE.getSize()));
        model.addAttribute("kw", params.get("kw"));
        return "/classroom/classroom-list";
    }

    @GetMapping("/add")
    public String addClassroom(Model model, Map<String, String> params) {
        Map<String, String> roleLecturer = new HashMap<>();
        roleLecturer.put("role", "ROLE_LECTURER");

        Map<Integer, GradeDetail> gradeMap = new HashMap<>();

        model.addAttribute("gradeMap", gradeMap);
        model.addAttribute("classroom", new Classroom());
        model.addAttribute("students", studentService.getStudents(null));
        model.addAttribute("courses", courseService.getCourses(null));
        model.addAttribute("semesters", semesterService.getSemesters(null));
        model.addAttribute("lecturers", userService.getUsers(roleLecturer));

        return "/classroom/classroom-form";
    }

    @GetMapping("/{id}")
    public String updateClassroom(@PathVariable("id") Integer id, Model model) {
        Map<String, String> roleLecturer = new HashMap<>();
        roleLecturer.put("role", "ROLE_LECTURER");

        Classroom classroom = classroomService.getClassroomWithStudents(id);
        if (classroom.getStudentSet() == null) {
            classroom.setStudentSet(new HashSet<>());
        }

        Map<Integer, GradeDetail> gradeMap = new HashMap<>();
        for (Student s : classroom.getStudentSet()) {
            Map<String, Integer> ref = new HashMap<>();
            ref.put("classroomId", classroom.getId());
            ref.put("studentId", s.getId());
            List<GradeDetail> gradeDetails = gradeDetailService.getGradeDetail(ref);
            GradeDetail gd = null;

            if (!gradeDetails.isEmpty()) {
                gd = gradeDetails.get(0);
            } else {
                gd = new GradeDetail();
                gd.setMidtermGrade(null);
                gd.setFinalGrade(null);
                gd.setExtraGradeSet(new HashSet<>());
            }
            gradeMap.put(s.getId(), gd);
        }

        model.addAttribute("classroom", classroom);
        model.addAttribute("gradeMap", gradeMap);
        model.addAttribute("students", studentService.getStudents(null));
        model.addAttribute("courses", courseService.getCourses(null));
        model.addAttribute("semesters", semesterService.getSemesters(null));
        model.addAttribute("lecturers", userService.getUsers(roleLecturer));

        return "/classroom/classroom-form";
    }

    @PostMapping("")
    public String saveClassroom(@ModelAttribute @Valid Classroom classroom, BindingResult bindingResult, Model model,
            @RequestParam(name = "studentIds", required = false) List<Integer> studentIds,
            @RequestParam Map<String, String> allParams) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Có lỗi xảy ra");
            return "/classroom/classroom-form";
        }

        Classroom savedClassroom = this.classroomService.saveClassroom(classroom, studentIds);

        Set<Student> allStudents = this.classroomService.getClassroomWithStudents(savedClassroom.getId()).getStudentSet();

        if (allStudents != null) {
            for (Student student : allStudents) {
                gradeDetailService.saveGradesForStudent(student, savedClassroom, allParams);
            }
        }

        return "redirect:/classrooms";
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClassroom(@PathVariable("id") Integer id) {
        try {
            classroomService.deleteClassroom(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Không thể xóa lớp.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Đã xảy ra lỗi: " + e.getMessage());
        }
    }

    @DeleteMapping("/{classId}/students/{studentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeStudentFromClass(
            @PathVariable("classId") Integer classId,
            @PathVariable("studentId") Integer studentId) {
        classroomService.removeStudentFromClassroom(classId, studentId);
    }

}
