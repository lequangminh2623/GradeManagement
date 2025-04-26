package com.mh.controllers;

import com.mh.pojo.Student;
import com.mh.pojo.dto.GradeDTO;
import com.mh.pojo.dto.TranscriptDTO;
import com.mh.services.ClassroomService;
import com.mh.services.GradeDetailService;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/secure/classrooms")
@CrossOrigin
public class ApiClassroomController {

    @Autowired
    private GradeDetailService gradeDetailService;
    @Autowired
    private ClassroomService classroomService;

    @PostMapping("/{classroomId}/grades")
    public ResponseEntity<?> saveGradesForClassroom(
            @PathVariable("classroomId") Integer classroomId,
            @RequestBody List<GradeDTO> gradeRequests) {

        // Tìm số lượng extraGrades lớn nhất trong các yêu cầu
        int maxExtraCount = gradeRequests.stream()
                .mapToInt(req -> req.getExtraGrades() == null ? 0 : req.getExtraGrades().size())
                .max()
                .orElse(0);

        // Lấy danh sách tất cả sinh viên trong lớp
        Set<Student> allStudent = classroomService.getClassroomWithStudents(classroomId).getStudentSet();

        // Xử lý các yêu cầu của sinh viên trong gradeRequests
        for (GradeDTO req : gradeRequests) {
            List<Double> extra = req.getExtraGrades();
            if (extra == null) {
                extra = new ArrayList<>();
            }
            int currentCount = extra.size();
            if (currentCount < maxExtraCount) {
                for (int i = currentCount; i < maxExtraCount; i++) {
                    extra.add(null);
                }
            } else if (currentCount > maxExtraCount) {
                extra = extra.subList(0, maxExtraCount);
            }
            req.setExtraGrades(extra);

            // Lưu điểm cho sinh viên trong lớp
            gradeDetailService.saveGradesForStudent(
                    req.getStudentId(),
                    classroomId,
                    req.getMidtermGrade(),
                    req.getFinalGrade(),
                    req.getExtraGrades()
            );
        }

        // Xử lý các sinh viên không có trong gradeRequests
        for (Student student : allStudent) {
            // Kiểm tra xem sinh viên đã có trong gradeRequests chưa
            boolean studentExists = gradeRequests.stream()
                    .anyMatch(grade -> grade.getStudentId().equals(student.getId()));

            if (!studentExists) {
                // Nếu sinh viên không có trong gradeRequests, tạo đối tượng GradeDTO mới với điểm mặc định
                GradeDTO defaultGradeDTO = new GradeDTO(student.getId(), null, null, new ArrayList<>());

                // Thêm null cho các điểm extraGrades cho sinh viên này
                List<Double> extra = defaultGradeDTO.getExtraGrades();
                for (int i = extra.size(); i < maxExtraCount; i++) {
                    extra.add(null);
                }

                // Lưu điểm cho sinh viên không có trong gradeRequests
                gradeDetailService.saveGradesForStudent(
                        defaultGradeDTO.getStudentId(),
                        classroomId,
                        defaultGradeDTO.getMidtermGrade(),
                        defaultGradeDTO.getFinalGrade(),
                        defaultGradeDTO.getExtraGrades()
                );
            }
        }

        return ResponseEntity.ok("Grades for classroom " + classroomId + " saved successfully");
    }

    @GetMapping("/{classroomId}/grades")
    public ResponseEntity<TranscriptDTO> getGradeSheetForClassroom(@PathVariable("classroomId") Integer classroomId) {
        TranscriptDTO gradeSheet = gradeDetailService.getTranscriptForClassroom(classroomId);
        return ResponseEntity.ok(gradeSheet);
    }

    @PostMapping("/{classroomId}/grades/upload")
    public ResponseEntity<?> uploadGradesCsv(
            @PathVariable("classroomId") Integer classroomId,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String headerLine = reader.readLine(); // đọc dòng header
            if (headerLine == null) {
                return ResponseEntity.badRequest().body("CSV header is missing");
            }

            String[] headers = headerLine.split(",");
            int extraStartIndex = 3; // từ cột thứ 4 trở đi là extra grades

            List<GradeDTO> gradeRequests = new ArrayList<>();
            int maxExtraCount = 0;

            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");

                Integer studentId = Integer.valueOf(tokens[0].trim());
                Double midtermGrade = parseDoubleSafe(tokens[1]);
                Double finalGrade = parseDoubleSafe(tokens[2]);

                // Lấy các extra grades từ cột extra1, extra2, ...
                List<Double> extraGrades = new ArrayList<>();
                for (int i = extraStartIndex; i < tokens.length; i++) {
                    extraGrades.add(parseDoubleSafe(tokens[i]));
                }

                // Cập nhật maxExtraCount nếu có extra grades nhiều hơn
                maxExtraCount = Math.max(maxExtraCount, extraGrades.size());

                // Lưu GradeDTO vào danh sách
                gradeRequests.add(new GradeDTO(studentId, midtermGrade, finalGrade, extraGrades));
            }

            // Lấy danh sách tất cả sinh viên trong lớp từ cơ sở dữ liệu (giả sử bạn có phương thức này)
            Set<Student> allStudent = classroomService.getClassroomWithStudents(classroomId).getStudentSet();

            // Điều chỉnh sao cho tất cả sinh viên có số lượng extra grades bằng nhau
            for (Student student : allStudent) {
                // Tìm sinh viên trong danh sách gradeRequests
                GradeDTO existingGradeDTO = gradeRequests.stream()
                        .filter(grade -> grade.getStudentId().equals(student.getId()))
                        .findFirst()
                        .orElse(null);

                if (existingGradeDTO == null) {
                    // Sinh viên không có trong file CSV, tạo một GradeDTO mới với điểm mặc định (null cho extraGrades)
                    existingGradeDTO = new GradeDTO(student.getId(), null, null, new ArrayList<>());
                    gradeRequests.add(existingGradeDTO);
                }

                List<Double> extra = existingGradeDTO.getExtraGrades();
                if (extra == null) {
                    extra = new ArrayList<>();
                }
                int currentCount = extra.size();
                if (currentCount < maxExtraCount) {
                    for (int i = currentCount; i < maxExtraCount; i++) {
                        extra.add(null);  // Thêm null cho các điểm chưa có
                    }
                } else if (currentCount > maxExtraCount) {
                    extra = extra.subList(0, maxExtraCount);  // Cắt bớt nếu có điểm extra thừa
                }
                existingGradeDTO.setExtraGrades(extra);
            }

            // Lưu điểm cho tất cả sinh viên
            for (GradeDTO req : gradeRequests) {
                gradeDetailService.saveGradesForStudent(
                        req.getStudentId(),
                        classroomId,
                        req.getMidtermGrade(),
                        req.getFinalGrade(),
                        req.getExtraGrades()
                );
            }

            return ResponseEntity.ok("CSV uploaded and grades saved for classroom " + classroomId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file: " + e.getMessage());
        }
    }

    private Double parseDoubleSafe(String value) {
        try {
            return (value == null || value.trim().isEmpty()) ? null : Double.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
