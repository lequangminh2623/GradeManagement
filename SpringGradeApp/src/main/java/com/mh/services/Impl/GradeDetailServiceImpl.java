/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.services.Impl;

import com.mh.pojo.Classroom;
import com.mh.pojo.ExtraGrade;
import com.mh.pojo.GradeDetail;
import com.mh.pojo.Student;
import com.mh.repositories.GradeDetailRepository;
import com.mh.services.ExtraGradeService;
import com.mh.services.GradeDetailService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Le Quang Minh
 */
@Service
public class GradeDetailServiceImpl implements GradeDetailService {

    @Autowired
    private GradeDetailRepository gradeDetailRepo;

    @Autowired
    private ExtraGradeService extraGradeService;

    @Override
    public List<GradeDetail> getGradeDetail(Map<String, Integer> params) {
        return this.gradeDetailRepo.getGradeDetail(params);
    }

    @Override
    public void deleteGradeDetail(Integer id) {
        this.gradeDetailRepo.deleteGradeDetail(id);
    }

    @Override
    public GradeDetail saveGradeDetail(GradeDetail gd) {
        return this.gradeDetailRepo.saveGradeDetail(gd);
    }
    
    @Override
    public void saveGradesForStudent(Student student, Classroom savedClassroom, Map<String, String> allParams) {
        Integer studentId = student.getId();

        Map<String, Integer> ref = new HashMap<>();
        ref.put("classroomId", savedClassroom.getId());
        ref.put("studentId", studentId);

        List<GradeDetail> gradeDetails = this.getGradeDetail(ref);
        GradeDetail gd = gradeDetails.isEmpty() ? new GradeDetail() : gradeDetails.get(0);

        if (gradeDetails.isEmpty()) {
            gd.setStudent(student);
            gd.setCourse(savedClassroom.getCourse());
            gd.setSemester(savedClassroom.getSemester());
            this.saveGradeDetail(gd);
        }

        try {
            String midStr = allParams.get("midtermGrade[" + studentId + "]");
            String finalStr = allParams.get("finalGrade[" + studentId + "]");

            gd.setMidtermGrade(midStr != null && !midStr.trim().isEmpty() ? Double.valueOf(midStr.trim()) : null);
            gd.setFinalGrade(finalStr != null && !finalStr.trim().isEmpty() ? Double.valueOf(finalStr.trim()) : null);
        } catch (NumberFormatException ex) {
            System.err.println("Error parsing grades for studentId " + studentId + ": " + ex.getMessage());
        }

        Set<ExtraGrade> extraSet = new LinkedHashSet<>();

        Map<Integer, ExtraGrade> existingExtraGradeMap
                = (gd.getId() != null ? gd.getExtraGradeSet() : new HashSet<ExtraGrade>())
                        .stream()
                        .collect(Collectors.toMap(ExtraGrade::getGradeIndex, eg -> eg));

        Set<Integer> allIndices = new HashSet<>();

        for (String key : allParams.keySet()) {
            if (key.startsWith("extraPoints[" + studentId + "][")) {
                try {
                    int startIndex = key.indexOf("][") + 2;
                    int endIndex = key.lastIndexOf("]");
                    int index = Integer.parseInt(key.substring(startIndex, endIndex));
                    allIndices.add(index);
                } catch (Exception e) {
                    System.err.println("Không thể phân tích index từ key: " + key);
                }
            }
        }

        allIndices.addAll(existingExtraGradeMap.keySet());

        for (Integer i : allIndices) {
            String key = "extraPoints[" + studentId + "][" + i + "]";
            String valueStr = allParams.get(key);

            if (valueStr == null) {
                if (existingExtraGradeMap.containsKey(i)) {
                    extraSet.remove(existingExtraGradeMap.get(i));
                }
                continue;
            }

            // Tìm hoặc tạo mới
            ExtraGrade eg = existingExtraGradeMap.getOrDefault(i, new ExtraGrade());
            eg.setGradeIndex(i);

            if (valueStr.trim().isEmpty()) {
                eg.setGrade(null);
            } else {
                try {
                    eg.setGrade(Double.valueOf(valueStr.trim()));
                } catch (NumberFormatException ex) {
                    System.err.println("Invalid grade value for key: " + key + " - " + ex.getMessage());
                    eg.setGrade(null);
                }
            }

            eg.setGradeDetail(gd);
            extraSet.add(eg);
        }

        gd.setExtraGradeSet(extraSet);

        this.saveGradeDetail(gd);
    }
}
