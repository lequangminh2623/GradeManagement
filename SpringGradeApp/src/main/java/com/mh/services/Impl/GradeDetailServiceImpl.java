/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.services.Impl;

import com.mh.pojo.Classroom;
import com.mh.pojo.ExtraGrade;
import com.mh.pojo.GradeDetail;
import com.mh.pojo.Student;
import com.mh.pojo.dto.GradeDTO;
import com.mh.pojo.dto.TranscriptDTO;
import com.mh.repositories.GradeDetailRepository;
import com.mh.services.ClassroomService;
import com.mh.services.GradeDetailService;
import com.mh.services.StudentService;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
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
    private StudentService studentService;

    @Autowired
    private ClassroomService classroomService;

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
    public void saveGradesForStudent(Integer studentId, Integer classroomId,
            Double midtermGrade, Double finalGrade,
            List<Double> extraGrades) {
        Student student = studentService.getStudentByUserId(studentId);
        Classroom classroom = classroomService.getClassroomById(classroomId);
        if (student == null || classroom == null) {
            throw new IllegalArgumentException("Không tìm thấy sinh viên hoặc lớp học với id được cung cấp.");
        }
        Map<String, Integer> ref = new HashMap<>();
        ref.put("classroomId", classroomId);
        ref.put("studentId", studentId);

        List<GradeDetail> gradeDetails = this.getGradeDetail(ref);
        GradeDetail gd;
        if (gradeDetails.isEmpty()) {
            gd = new GradeDetail();
            gd.setStudent(student);
            gd.setCourse(classroom.getCourse());
            gd.setSemester(classroom.getSemester());
            this.saveGradeDetail(gd);
        } else {
            gd = gradeDetails.get(0);
        }
        gd.setMidtermGrade(midtermGrade);
        gd.setFinalGrade(finalGrade);
        gd.setUpdatedDate(new Date());

        if (extraGrades != null) {
            Map<Integer, ExtraGrade> existingMap = new HashMap<>();
            if (gd.getExtraGradeSet() != null) {
                for (ExtraGrade eg : gd.getExtraGradeSet()) {
                    existingMap.put(eg.getGradeIndex(), eg);
                }
            }

            Set<ExtraGrade> newExtraSet = new LinkedHashSet<>();
            for (int i = 0; i < extraGrades.size(); i++) {
                Double gradeValue = extraGrades.get(i);
                ExtraGrade eg;
                if (existingMap.containsKey(i)) {
                    eg = existingMap.get(i);
                    eg.setGrade(gradeValue);
                    eg.setGradeIndex(i);
                } else {
                    eg = new ExtraGrade();
                    eg.setGradeIndex(i);
                    eg.setGrade(gradeValue);
                    eg.setGradeDetail(gd);
                }
                newExtraSet.add(eg);
            }
            gd.setExtraGradeSet(newExtraSet);
        } else {
            gd.setExtraGradeSet(new LinkedHashSet<>());
        }

        this.saveGradeDetail(gd);
    }

    @Override
    public boolean existsByStudentAndCourseAndSemester(Integer studentId, Integer courseId, Integer semesterId, Integer excludeId) {
        return this.gradeDetailRepo.existsByStudentAndCourseAndSemester(studentId, courseId, semesterId, excludeId);
    }

    @Override
    public boolean existsByGradeDetailIdAndGradeIndex(Integer gradeDetailId, Integer gradeIndex, Integer currentExtraGradeId) {
        return this.gradeDetailRepo.existsByGradeDetailIdAndGradeIndex(gradeDetailId, gradeIndex, currentExtraGradeId);
    }
    
    @Override
    public TranscriptDTO getTranscriptForClassroom(Integer classroomId) {
        Classroom classroom = classroomService.getClassroomWithStudents(classroomId);
        if (classroom == null) {
            throw new EntityNotFoundException("Không tìm thấy lớp học với id: " + classroomId);
        }

        TranscriptDTO transcriptDTO = new TranscriptDTO();
        transcriptDTO.setClassroomName(classroom.getName());
        transcriptDTO.setAcademicTerm(classroom.getSemester().getAcademicYear().getYear() 
                + " - " + classroom.getSemester().getSemesterType());
        transcriptDTO.setCourseName(classroom.getCourse().getName());
        transcriptDTO.setLecturerName(classroom.getLecturer().getLastName() + " " + classroom.getLecturer().getFirstName());
        transcriptDTO.setGradeStatus(classroom.getGradeStatus());
        
        List<GradeDTO> gradeDTOList = new ArrayList<>();

        if (classroom.getStudentSet() != null) {
            for (Student student : classroom.getStudentSet()) {
                GradeDTO gradeDTO = new GradeDTO();
                gradeDTO.setStudentId(student.getId());
                gradeDTO.setStudentCode(student.getCode());
                gradeDTO.setFullName(student.getUser().getLastName() + " " + student.getUser().getFirstName());

                Map<String, Integer> ref = new HashMap<>();
                ref.put("classroomId", classroomId);
                ref.put("studentId", student.getId());

                GradeDetail gradeDetail = this.getGradeDetail(ref).get(0);

                if (gradeDetail != null) {
                    gradeDTO.setMidtermGrade(gradeDetail.getMidtermGrade());
                    gradeDTO.setFinalGrade(gradeDetail.getFinalGrade());

                    List<Double> extraGrades = gradeDetail.getExtraGradeSet().stream()
                            .sorted(Comparator.comparing(ExtraGrade::getGradeIndex))
                            .map(ExtraGrade::getGrade)
                            .collect(Collectors.toList());
                    gradeDTO.setExtraGrades(extraGrades);
                } else {
                    gradeDTO.setMidtermGrade(null);
                    gradeDTO.setFinalGrade(null);
                    gradeDTO.setExtraGrades(new ArrayList<>());
                }
                gradeDTOList.add(gradeDTO);
            }
        }

        transcriptDTO.setStudents(gradeDTOList);
        return transcriptDTO;
    }
}
