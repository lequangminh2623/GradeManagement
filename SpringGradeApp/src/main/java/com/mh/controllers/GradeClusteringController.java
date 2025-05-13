package com.mh.controllers;

import com.mh.pojo.dto.SemesterAnalysisResult;
import com.mh.services.GradeDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class GradeClusteringController {

    @Autowired
    private GradeDetailService gradeDetailService;

    @GetMapping("/analysis/{semesterId}")
    public SemesterAnalysisResult clusterStudents(@PathVariable("semesterId") Integer semesterId) {
        return gradeDetailService.analyzeSemester(semesterId);
    }
}
