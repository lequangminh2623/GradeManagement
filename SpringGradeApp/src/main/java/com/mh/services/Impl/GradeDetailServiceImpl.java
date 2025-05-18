/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.services.Impl;

import com.mh.pojo.Classroom;
import com.mh.pojo.ExtraGrade;
import com.mh.pojo.GradeDetail;
import com.mh.pojo.Student;
import com.mh.pojo.dto.GradeClusterResultDTO;
import com.mh.pojo.dto.GradeDTO;
import com.mh.pojo.dto.GradeDetailDTO;
import com.mh.pojo.dto.SemesterAnalysisResult;
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
import smile.clustering.KMeans;

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
    public TranscriptDTO getTranscriptForClassroom(Integer classroomId, Map<String, String> params) {
        Classroom classroom = classroomService.getClassroomById(classroomId);
        List<Student> students = classroomService.getStudentsInClassroom(classroom.getId(), params);

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
            for (Student student : students) {
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

        if (grade != null && (grade < 0 || grade > 10)) {
            throw new IllegalArgumentException("Điểm phải nằm trong khoảng từ 0 đến 10.");
        }
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

    @Override
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

    @Override
    public List<GradeDetailDTO> getGradesByStudent(Integer userId, Map<String, String> params) {
        return this.gradeDetailRepo.getGradeDetailByStudent(userId, params);
    }

    @Override
    public List<GradeDetail> getGradeDetailsBySemester(Integer semesterId) {
        return this.gradeDetailRepo.getGradeDetailsBySemester(semesterId);
    }

    @Override
    public SemesterAnalysisResult analyzeSemester(List<GradeDetail> gradeDetails) {

        // 2. Chuyển thành GradeDTO
        List<GradeDTO> gradeList = gradeDetails.stream().map(grade -> {
            GradeDTO dto = new GradeDTO();
            dto.setStudentId(grade.getStudent().getId());
            dto.setStudentCode(grade.getStudent().getCode());
            dto.setFullName(grade.getStudent().getUser().getLastName() + " " + grade.getStudent().getUser().getFirstName());
            dto.setMidtermGrade(grade.getMidtermGrade());
            dto.setFinalGrade(grade.getFinalGrade());
            List<Double> extra = grade.getExtraGradeSet().stream()
                    .sorted(Comparator.comparingInt(ExtraGrade::getGradeIndex))
                    .map(ExtraGrade::getGrade)
                    .collect(Collectors.toList());
            dto.setExtraGrades(extra);
            return dto;
        }).collect(Collectors.toList());

        // 3. Xác định số cột extra tối đa để pad vào ma trận
        int maxExtra = gradeList.stream()
                .mapToInt(g -> g.getExtraGrades() == null ? 0 : g.getExtraGrades().size())
                .max().orElse(0);

        // 4. Xây dựng ma trận dữ liệu với padding
        double[][] data = gradeList.stream().map(g -> {
            List<Double> features = new ArrayList<>();
            features.add(g.getMidtermGrade() != null ? g.getMidtermGrade() : 0.0);
            features.add(g.getFinalGrade() != null ? g.getFinalGrade() : 0.0);
            List<Double> extras = g.getExtraGrades() == null ? Collections.emptyList() : g.getExtraGrades();
            for (int i = 0; i < maxExtra; i++) {
                features.add(i < extras.size() ? (extras.get(i) != null ? extras.get(i) : 0.0) : 0.0);
            }
            return features.stream().mapToDouble(Double::doubleValue).toArray();
        }).toArray(double[][]::new);

        if (gradeDetails.isEmpty()) {
            SemesterAnalysisResult emptyResult = new SemesterAnalysisResult();
            emptyResult.setTotalStudents(0);
            emptyResult.setWeakStudents(0);
            emptyResult.setWeakRatio(0);
            emptyResult.setWeakStudentList(Collections.emptyList());
            emptyResult.setCourseWeakRatios(Collections.emptyMap());
            emptyResult.setCriticalCourses(Collections.emptyList());
            return emptyResult;
        }

        // 5. Chạy KMeans phân cụm 2 nhóm
        KMeans kmeans = KMeans.fit(data, 2);
        int[] labels = kmeans.y;

        // 6. Tính điểm trung bình mỗi cụm để xác định nhóm yếu
        Map<Integer, List<double[]>> clusters = new HashMap<>();
        for (int i = 0; i < labels.length; i++) {
            clusters.computeIfAbsent(labels[i], k -> new ArrayList<>()).add(data[i]);
        }
        Map<Integer, Double> clusterAvg = new HashMap<>();
        for (Map.Entry<Integer, List<double[]>> entry : clusters.entrySet()) {
            double sum = 0;
            int count = 0;
            for (double[] vec : entry.getValue()) {
                for (double v : vec) {
                    sum += v;
                    count++;
                }
            }
            clusterAvg.put(entry.getKey(), sum / count);
        }
        // Nhóm có avg nhỏ hơn là yếu
        int weakCluster = (clusterAvg.get(0) == null ? 0 : clusterAvg.get(0)) <= (clusterAvg.get(1) == null ? 0 : clusterAvg.get(1)) ? 0 : 1;

        // 7. Mapping kết quả với nhãn cố định: 0 = yếu, 1 = giỏi
        List<GradeClusterResultDTO> clusterResults = new ArrayList<>();
        for (int i = 0; i < gradeList.size(); i++) {
            GradeDTO g = gradeList.get(i);
            GradeClusterResultDTO dto = new GradeClusterResultDTO();
            dto.setStudentId(g.getStudentId());
            dto.setStudentCode(g.getStudentCode());
            dto.setFullName(g.getFullName());
            dto.setCourseName(gradeDetails.get(i).getCourse().getName());
            // gán nhãn: 0 = yếu, 1 = giỏi
            dto.setCluster(labels[i] == weakCluster ? 0 : 1);
            clusterResults.add(dto);
        }

        // 8. Trả về kết quả phân tích học kỳ
        return buildSemesterAnalysis(clusterResults);
    }

    public SemesterAnalysisResult buildSemesterAnalysis(List<GradeClusterResultDTO> clusterResults) {
        SemesterAnalysisResult result = new SemesterAnalysisResult();
        int total = clusterResults.size();
        List<GradeClusterResultDTO> weakStudents = clusterResults.stream()
                .filter(r -> r.getCluster() == 0)
                .collect(Collectors.toList());
        int weak = weakStudents.size();
        result.setTotalStudents(total);
        result.setWeakStudents(weak);
        result.setWeakRatio(total == 0 ? 0 : (weak * 100.0 / total));
        result.setWeakStudentList(weakStudents);
        Map<String, List<GradeClusterResultDTO>> byCourse = clusterResults.stream()
                .collect(Collectors.groupingBy(GradeClusterResultDTO::getCourseName));
        Map<String, Double> courseWeakRatios = new HashMap<>();
        List<String> criticalCourses = new ArrayList<>();
        byCourse.forEach((course, list) -> {
            long cnt = list.stream().filter(r -> r.getCluster() == 0).count();
            double ratio = list.isEmpty() ? 0 : (cnt * 100.0 / list.size());
            courseWeakRatios.put(course, ratio);
            if (ratio >= 40.0) {
                criticalCourses.add(course);
            }
        });
        result.setCourseWeakRatios(courseWeakRatios);
        result.setCriticalCourses(criticalCourses);
        return result;
    }

    @Override
    public List<GradeDetail> getGradeDetailsByLecturerAndSemester(Integer lecturerId, Integer semesterId) {
        return this.gradeDetailRepo.getGradeDetailsByLecturerAndSemester(lecturerId, semesterId);
    }

}
