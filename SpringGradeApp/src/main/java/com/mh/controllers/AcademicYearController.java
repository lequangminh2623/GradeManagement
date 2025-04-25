/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.controllers;

import com.mh.pojo.AcademicYear;
import com.mh.pojo.Semester;
import com.mh.services.AcademicYearService;
import com.mh.services.SemesterService;
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
public class AcademicYearController {
    
    @Autowired
    private AcademicYearService academicYearService;
    
    @Autowired
    private SemesterService semesterService;
    
    @Autowired
    @Qualifier("webAppValidator")
    private WebAppValidator webAppValidator;
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(webAppValidator);
    }
    
    @GetMapping("/years")
    public String getAcademicYears(Model model, @RequestParam Map<String, String> params) {
        String page = params.get("page");
        
        if (page == null || page.isEmpty()) {
            params.put("page", "1");
        }
        
        List<AcademicYear> years = this.academicYearService.getAcademicYears(params);
        model.addAttribute("years", years);
        model.addAttribute("currentPage", Integer.valueOf(params.get("page")));
        model.addAttribute("totalPages", (int) Math.ceil((double) this.academicYearService.countYears(params) / PageSize.YEAR_PAGE_SIZE.getSize()));
        model.addAttribute("kw", params.get("kw"));
        
        return "/year/year-list";
    }
    
    @GetMapping("/years/add")
    public String addYear(Model model) {
        AcademicYear academicYear = new AcademicYear();
        model.addAttribute("academicYear", academicYear);
        return "/year/year-form";
    }
    
    @PostMapping("/years")
    public String saveYear(@ModelAttribute("academicYear") @Valid AcademicYear academicYear, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Có lỗi xảy ra");
            return "/year/year-form";
        }
        
        this.academicYearService.saveYear(academicYear);
        return "redirect:/years";
        
    }
    
    @GetMapping("/years/{id}")
    public String updateYear(Model model, @PathVariable(value = "id") int id) {
        AcademicYear academicYear = this.academicYearService.getYearById(id);
        model.addAttribute("academicYear", academicYear);
        return "/year/year-form";
    }
    
    @DeleteMapping("/years/{id}")
    public ResponseEntity<String> deleteYear(@PathVariable("id") int id) {
        try {
            this.academicYearService.deleteYearById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Không thể xóa năm học này.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Đã xảy ra lỗi: " + e.getMessage());
        }
    }

    // Semester
    @GetMapping("/years/{yearId}/semesters")
    public String getSemesters(Model model, @PathVariable(value = "yearId") int yearId, @RequestParam Map<String, String> params) {
        AcademicYear year = this.academicYearService.getYearById(yearId);
        if (year != null) {
            model.addAttribute("year", year);
            model.addAttribute("semesters", this.semesterService.getSemestersByAcademicYearId(yearId, params));
            model.addAttribute("kw", params.get("kw"));
        }
        return "/year/semester-list";
    }
    
    @GetMapping("/years/{yearId}/semesters/add")
    public String addSemester(Model model, @PathVariable(value = "yearId") int yearId) {
        AcademicYear year = this.academicYearService.getYearById(yearId);
        if (year != null) {
            Semester semester = new Semester();
            semester.setAcademicYear(year);
            model.addAttribute("semester", semester);
            model.addAttribute("year", year);
        }
        return "/year/semester-form";
    }
    
    @PostMapping("/years/{yearId}/semesters")
    public String saveSemester(@ModelAttribute("semester") @Valid Semester semester, BindingResult bindingResult,
            @PathVariable(value = "yearId") int yearId, Model model) {
        
        AcademicYear year = this.academicYearService.getYearById(yearId);
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Có lỗi xảy ra");
            model.addAttribute("year", year);
            return "/year/semester-form";
        }
        
        this.semesterService.saveSemester(semester);
        return "redirect:/years/{yearId}/semesters";
        
    }
    
    @GetMapping("/years/{yearId}/semesters/{semesterId}")
    public String updateSemester(Model model, @PathVariable(value = "yearId") int yearId,
            @PathVariable(value = "semesterId") int semesterId) {
        AcademicYear year = this.academicYearService.getYearById(yearId);
        if (year != null) {
            Semester semester = this.semesterService.getSemesterById(semesterId);
            model.addAttribute("semester", semester);
            model.addAttribute("year", year);
        }
        return "/year/semester-form";
    }
    
    @DeleteMapping("/years/{yearId}/semesters/{semesterId}")
    public ResponseEntity<String> deleteSemester(Model model, @PathVariable(value = "semesterId") int semesterId) {
        try {
            this.semesterService.deleteSemesterById(semesterId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Không thể xóa học kỳ này.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Đã xảy ra lỗi: " + e.getMessage());
        }
    }
    
}
