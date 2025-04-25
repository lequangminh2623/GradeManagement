package com.mh.controllers;

import com.mh.pojo.Classroom;
import com.mh.pojo.dto.GradeDTO;
import com.mh.pojo.dto.TranscriptDTO;
import com.mh.services.ClassroomService;
import com.mh.services.GradeDetailService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        int maxExtraCount = gradeRequests.stream()
                .mapToInt(req -> req.getExtraGrades() == null ? 0 : req.getExtraGrades().size())
                .max()
                .orElse(0);

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
        }

        for (GradeDTO req : gradeRequests) {
            gradeDetailService.saveGradesForStudent(
                    req.getStudentId(),
                    classroomId,
                    req.getMidtermGrade(),
                    req.getFinalGrade(),
                    req.getExtraGrades()
            );
        }

        return ResponseEntity.ok("Điểm của lớp  " + classroomId + " lưu thành công!");
    }

    @GetMapping("/{classroomId}/grades")
    public ResponseEntity<TranscriptDTO> getTranscriptForClassroom(@PathVariable("classroomId") Integer classroomId) {
        TranscriptDTO gradeSheet = gradeDetailService.getGradeSheetForClassroom(classroomId);
        return ResponseEntity.ok(gradeSheet);
    }
    
    @PatchMapping("/{classroomId}/lock")
    public ResponseEntity<?> lockTranscript(@PathVariable("classroomId") Integer classroomId) {
        Classroom classroom = classroomService.getClassroomById(classroomId);
        classroom.setGradeStatus("LOCKED");
        classroomService.saveClassroom(classroom);
        
        return ResponseEntity.ok("Điểm của lớp " + classroomId + " khóa thành công!");
    }

}