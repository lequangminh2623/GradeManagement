/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.controllers;

import com.mh.pojo.GradeDetail;
import com.mh.pojo.User;
import com.mh.services.GradeDetailService;
import com.mh.services.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Le Quang Minh
 */
@RestController
@RequestMapping("/api/secure/grades")
@CrossOrigin
public class ApiGradeController {

    @Autowired
    private GradeDetailService gradeDetailService;

    @Autowired
    private UserService userService;

    @GetMapping("/student")
    public ResponseEntity<?> getStudentGrades(Authentication authentication) {
        User currentUser = userService.getUserByEmail(authentication.getName());

        if (currentUser.getStudent() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Bạn không có quyền truy cập");
        }

        List<GradeDetail> gradeSheet = gradeDetailService.getGradesByStudent(currentUser.getStudent().getId());
        return ResponseEntity.ok(gradeSheet);

    }
}
