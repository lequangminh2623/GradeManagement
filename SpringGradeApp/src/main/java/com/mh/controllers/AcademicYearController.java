/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.controllers;

import com.mh.pojo.AcademicYear;
import com.mh.services.AcademicYearService;
import com.mh.utils.ExceptionUtils;
import com.mh.utils.PageSize;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author leoma
 */
@Controller
public class AcademicYearController {

    @Autowired
    private AcademicYearService academicYearService;

    @GetMapping("/years")
    public String getAcademicYears(Model model, @RequestParam Map<String, String> params) {
        String page = params.get("page");

        if (page == null || page.isEmpty()) {
            params.put("page", "1");
        }

        List<AcademicYear> years = this.academicYearService.getAcademicYears(params);
        model.addAttribute("years", years);
        model.addAttribute("currentPage", Integer.parseInt(params.get("page")));
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

        try {
            this.academicYearService.saveYear(academicYear);
            return "redirect:/years";
        } catch (Exception e) {
            Throwable root = ExceptionUtils.getRootCause(e);
            if (root instanceof java.sql.SQLIntegrityConstraintViolationException && root.getMessage().contains("Duplicate entry")) {
                if (root.getMessage().contains("academic_year.year")) {
                    model.addAttribute("errorMessage", "Năm học này đã tồn tại.");
                }
            } else {
                model.addAttribute("errorMessage", "Đã xảy ra lỗi: " + root.getMessage());
            }
            return "/year/year-form";
        }

    }

    @GetMapping("/years/{id}")
    public String updateYear(Model model, @PathVariable(value = "id") int id) {
        AcademicYear academicYear = this.academicYearService.getYearById(id);
        model.addAttribute("academicYear", academicYear);
        return "/year/year-form";
    }

    @DeleteMapping("/years/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable("id") int id) {
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
}
