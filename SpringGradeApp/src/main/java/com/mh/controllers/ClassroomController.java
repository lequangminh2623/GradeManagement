/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.controllers;

import com.mh.pojo.Student;
import com.mh.pojo.Classroom;
import com.mh.pojo.ExtraGrade;
import com.mh.pojo.GradeDetail;
import com.mh.pojo.dto.GradeDTO;
import com.mh.pojo.dto.TranscriptDTO;
import com.mh.services.StudentService;
import com.mh.services.ClassroomService;
import com.mh.services.CourseService;
import com.mh.services.GradeDetailService;
import com.mh.services.SemesterService;
import com.mh.services.UserService;
import com.mh.utils.PageSize;
import com.mh.validators.WebAppValidator;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    @Qualifier("webAppValidator")
    private WebAppValidator webAppValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(webAppValidator);
    }

    @ModelAttribute
    public void commonAttributes(Model model) {
        Map<String, String> roleLecturer = new HashMap<>();
        roleLecturer.put("role", "ROLE_LECTURER");

        model.addAttribute("students", studentService.getStudents(null));
        model.addAttribute("courses", courseService.getCourses(null));
        model.addAttribute("semesters", semesterService.getSemesters(null));
        model.addAttribute("lecturers", userService.getUsers(roleLecturer));
    }

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
    public String addClassroom(Model model) {
        model.addAttribute("transcript", new TranscriptDTO());
        model.addAttribute("classroom", new Classroom());
        return "/classroom/classroom-form";
    }

    @GetMapping("/{id}")
    public String updateClassroom(@PathVariable("id") Integer id, Model model) {
        Classroom classroom = classroomService.getClassroomWithStudents(id);
        if (classroom.getStudentSet() == null) {
            classroom.setStudentSet(new HashSet<>());
        }

        List<GradeDTO> gradeDTOList = new ArrayList<>();
        for (Student s : classroom.getStudentSet()) {
            Map<String, Integer> ref = Map.of("classroomId", classroom.getId(), "studentId", s.getId());
            List<GradeDetail> gradeDetails = gradeDetailService.getGradeDetail(ref);
            GradeDetail gd = !gradeDetails.isEmpty() ? gradeDetails.get(0) : new GradeDetail();
            if (gd.getExtraGradeSet() == null) {
                gd.setExtraGradeSet(new HashSet<>());
            }

            GradeDTO dto = new GradeDTO();
            dto.setStudentId(s.getId());
            dto.setStudentCode(s.getCode());
            dto.setFullName(s.getUser().getLastName() + " " + s.getUser().getFirstName());
            dto.setMidtermGrade(gd.getMidtermGrade());
            dto.setFinalGrade(gd.getFinalGrade());
            dto.setExtraGrades(gd.getExtraGradeSet().stream()
                    .sorted(Comparator.comparingInt(ExtraGrade::getGradeIndex))
                    .map(ExtraGrade::getGrade)
                    .collect(Collectors.toList()));
            gradeDTOList.add(dto);
        }

        TranscriptDTO transcript = new TranscriptDTO();
        transcript.setClassroomName(classroom.getCourse().getName() + " - " + classroom.getName());
        transcript.setCourseName(classroom.getCourse().getName());
        transcript.setAcademicTerm(classroom.getSemester().getSemesterType());
        transcript.setLecturerName(classroom.getLecturer().getLastName() + " " + classroom.getLecturer().getFirstName());
        transcript.setGrades(gradeDTOList);

        model.addAttribute("classroom", classroom);
        model.addAttribute("transcript", transcript);
        return "/classroom/classroom-form";
    }

    @PostMapping("")
    public String saveClassroomAndStudents(
            @ModelAttribute @Valid Classroom classroom,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Có lỗi xảy ra");

            if (classroom.getStudentSet() == null) {
                classroom.setStudentSet(new HashSet<>());
            }

            TranscriptDTO transcript = new TranscriptDTO();
            if (classroom.getId() != null) {
                List<GradeDTO> gradeDTOList = new ArrayList<>();
                for (Student s : classroom.getStudentSet()) {
                    Map<String, Integer> ref = Map.of("classroomId", classroom.getId(), "studentId", s.getId());
                    List<GradeDetail> gradeDetails = gradeDetailService.getGradeDetail(ref);
                    GradeDetail gd = !gradeDetails.isEmpty() ? gradeDetails.get(0) : new GradeDetail();
                    if (gd.getExtraGradeSet() == null) {
                        gd.setExtraGradeSet(new HashSet<>());
                    }

                    GradeDTO dto = new GradeDTO();
                    dto.setStudentId(s.getId());
                    dto.setStudentCode(s.getCode());
                    dto.setFullName(s.getUser().getLastName() + " " + s.getUser().getFirstName());
                    dto.setMidtermGrade(gd.getMidtermGrade());
                    dto.setFinalGrade(gd.getFinalGrade());
                    dto.setExtraGrades(gd.getExtraGradeSet().stream()
                            .sorted(Comparator.comparingInt(ExtraGrade::getGradeIndex))
                            .map(ExtraGrade::getGrade)
                            .collect(Collectors.toList()));
                    gradeDTOList.add(dto);
                }
                transcript.setClassroomName(classroom.getCourse().getName() + " - " + classroom.getName());
                transcript.setCourseName(classroom.getCourse().getName());
                transcript.setAcademicTerm(classroom.getSemester().getSemesterType());
                transcript.setLecturerName(classroom.getLecturer().getLastName() + " " + classroom.getLecturer().getFirstName());
                transcript.setGrades(gradeDTOList);
            }
            model.addAttribute("transcript", transcript);
            model.addAttribute("classroom", classroom);
            return "/classroom/classroom-form";
        }

        Classroom savedClassroom = classroomService.saveClassroom(classroom);
        gradeDetailService.initGradeDetailsForClassroom(savedClassroom);

        return "redirect:/classrooms";
    }

    @PostMapping("/{id}/grades")
    public String saveGrades(@PathVariable("id") Integer classroomId,
            @ModelAttribute("transcript") @Valid TranscriptDTO transcript,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Có lỗi xảy ra");
            Classroom classroom = classroomService.getClassroomWithStudents(classroomId);
            model.addAttribute("classroom", classroom);
            model.addAttribute("transcript", transcript);
            return "/classroom/classroom-form";
        }
         try {
            gradeDetailService.updateGradesForClassroom(classroomId, transcript.getGrades());
        } catch (IllegalArgumentException e) {
             model.addAttribute("errorMessage", "Không thể có nhiều hơn 3 cột diểm bổ sung");
            Classroom classroom = classroomService.getClassroomWithStudents(classroomId);
            model.addAttribute("classroom", classroom);
            model.addAttribute("transcript", transcript);
            return "/classroom/classroom-form";
        }
        

        return "redirect:/classrooms";
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClassroom(@PathVariable("id") Integer id) {
        try {
            classroomService.deleteClassroom(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Không thể xóa lớp do lớp còn sinh viên.");
        } catch (PersistenceException | ConstraintViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Không thể xóa lớp do ràng buộc dữ liệu.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Đã xảy ra lỗi: " + e.getMessage());
        }
    }

    @DeleteMapping("/{classId}/students/{studentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeStudentFromClass(@PathVariable("classId") Integer classId,
            @PathVariable("studentId") Integer studentId) {
        Map<String, Integer> ref = new HashMap<>();
        ref.put("classroomId", classId);
        ref.put("studentId", studentId);
        List<GradeDetail> gradeDetails = this.gradeDetailService.getGradeDetail(ref);
        if (gradeDetails != null) {
            Integer gradeDetailId = gradeDetails.get(0).getId();
            classroomService.removeStudentFromClassroom(classId, studentId);
            this.gradeDetailService.deleteGradeDetail(gradeDetailId);
        }
    }
}
