/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mh.services;

import com.mh.pojo.GradeDetail;
import com.mh.pojo.dto.GradeDTO;
import com.mh.pojo.dto.GradeDetailDTO;
import com.mh.pojo.dto.TranscriptDTO;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Le Quang Minh
 */
public interface GradeDetailService {

    List<GradeDetail> getGradeDetail(Map<String, Integer> params);

    void deleteGradeDetail(Integer id);

    GradeDetail saveGradeDetail(GradeDetail gd);

    void saveGradesForStudent(Integer studentId, Integer classroomId, Double midtermGrade, Double finalGrade, List<Double> extraGrades);

    boolean existsByStudentAndCourseAndSemester(Integer studentId, Integer courseId, Integer semesterId, Integer excludeId);

    boolean existsByGradeDetailIdAndGradeIndex(Integer gradeDetailId, Integer gradeIndex, Integer currentExtraGradeId);

    TranscriptDTO getTranscriptForClassroom(Integer classroomId, Map<String, String> params);

    void updateGradesForClassroom(Integer classroomId, List<GradeDTO> gradeRequests);

    void uploadGradesFromCsv(Integer classroomId, MultipartFile file) throws IOException;

    List<GradeDTO> getGradesByClassroom(Integer classroomId);

    List<GradeDetailDTO> getGradesByStudent(Integer userId, Map<String, String> params);
}
