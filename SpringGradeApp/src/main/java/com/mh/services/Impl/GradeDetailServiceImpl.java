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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.web.multipart.MultipartFile;

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
                Double gradeValue = checkValidGrade(extraGrades.get(i));
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

    @Override
    public void updateGradesForClassroom(Integer classroomId, List<GradeDTO> gradeRequests) {
        int maxExtraCount = gradeRequests.stream()
                .mapToInt(req -> req.getExtraGrades() == null ? 0 : req.getExtraGrades().size())
                .max()
                .orElse(0);

        Set<Student> allStudents = classroomService.getClassroomWithStudents(classroomId)
                .getStudentSet();

        for (GradeDTO dto : gradeRequests) {
            List<Double> extra = dto.getExtraGrades() == null
                    ? new ArrayList<>()
                    : new ArrayList<>(dto.getExtraGrades());
            while (extra.size() < maxExtraCount) {
                extra.add(null);
            }
            if (extra.size() > maxExtraCount) {
                extra = extra.subList(0, maxExtraCount);
            }
            dto.setExtraGrades(extra);

            saveGradesForStudent(dto.getStudentId(), classroomId,
                    checkValidGrade(dto.getMidtermGrade()), checkValidGrade(dto.getFinalGrade()), extra);
        }

        for (Student student : allStudents) {
            boolean exists = gradeRequests.stream()
                    .anyMatch(dto -> dto.getStudentId().equals(student.getId()));
            if (!exists) {
                List<Double> emptyExtra = new ArrayList<>(Collections.nCopies(maxExtraCount, null));
                saveGradesForStudent(student.getId(), classroomId, null, null, emptyExtra);
            }
        }
    }
    
    private Double checkValidGrade(Double grade) {
        if(grade < 0 || grade > 10)
            throw new IllegalArgumentException("Điểm phải nằm trong khoảng từ 0 đến 10.");
        return grade;
    }

    private Double parseDoubleSafe(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            Double parsedValue = Double.valueOf(value.trim());
            if (parsedValue < 0 || parsedValue > 10) {
                throw new IllegalArgumentException("Điểm phải nằm trong khoảng từ 0 đến 10.");
            }
            return parsedValue;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Giá trị nhập không phải số hợp lệ.");
        }
    }

    @Override
    public void uploadGradesFromCsv(Integer classroomId, MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String header = reader.readLine();
            if (header == null) {
                throw new IllegalArgumentException("CSV header is missing");
            }

            int extraStart = 3;
            List<GradeDTO> gradeRequests = new ArrayList<>();
            String line;
            int maxExtraCount = 0;

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                Integer studentId = Integer.valueOf(tokens[0].trim());
                Double mid = parseDoubleSafe(tokens[1]);
                Double fin = parseDoubleSafe(tokens[2]);

                List<Double> extras = new ArrayList<>();
                for (int i = extraStart; i < tokens.length; i++) {
                    extras.add(parseDoubleSafe(tokens[i]));
                }
                maxExtraCount = Math.max(maxExtraCount, extras.size());
                gradeRequests.add(new GradeDTO(studentId, mid, fin, extras));
            }

            updateGradesForClassroom(classroomId, gradeRequests);
        }
    }

    public List<GradeDTO> getGradesByClassroom(Integer classroomId) {
        Set<Student> students = classroomService.getClassroomWithStudents(classroomId).getStudentSet();
        List<GradeDTO> result = new ArrayList<>();

        for (Student s : students) {
            Map<String, Integer> param = new HashMap<>();
            param.put("classroomId", classroomId);
            param.put("studentId", s.getId());
            GradeDetail gd = getGradeDetail(param).get(0);
            List<Double> extraGrades = gd != null && gd.getExtraGradeSet() != null
                    ? gd.getExtraGradeSet().stream()
                            .sorted(Comparator.comparingInt(ExtraGrade::getGradeIndex))
                            .map(ExtraGrade::getGrade)
                            .collect(Collectors.toList())
                    : new ArrayList<>();
            result.add(new GradeDTO(s.getId(), s.getCode(),
                    String.format("%s %s", s.getUser().getLastName(), s.getUser().getFirstName()),
                    gd != null ? gd.getMidtermGrade() : null,
                    gd != null ? gd.getFinalGrade() : null,
                    extraGrades));
        }
        return result;
    }

}
