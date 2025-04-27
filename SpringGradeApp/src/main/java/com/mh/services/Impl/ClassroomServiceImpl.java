/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.services.Impl;

import com.mh.pojo.Classroom;
import com.mh.pojo.ExtraGrade;
import com.mh.pojo.GradeDetail;
import com.mh.pojo.Student;
import com.mh.pojo.User;
import com.mh.repositories.ClassroomRepository;
import com.mh.services.ClassroomService;
import com.mh.services.GradeDetailService;
import com.mh.utils.MailUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Le Quang Minh
 */
@Service
public class ClassroomServiceImpl implements ClassroomService {

    @Autowired
    private ClassroomRepository classroomRepo;
    
    @Autowired
    private GradeDetailService gradeDetailService;
    
    @Autowired
    private MailUtils mailUtils;

    @Override
    public Classroom saveClassroom(Classroom classroom) {
        return this.classroomRepo.saveClassroom(classroom);
    }

    @Override
    public Classroom getClassroomById(Integer id) {
        return this.classroomRepo.getClassroomById(id);
    }

    @Override
    public void deleteClassroom(Integer id) {
        if (!classroomRepo.getClassroomWithStudents(id).getStudentSet().isEmpty()) {
            throw new IllegalStateException("Cannot delete classroom with enrolled students.");
        }
        this.classroomRepo.deleteClassroom(id);
    }

    @Override
    public List<Classroom> getClassrooms(Map<String, String> params) {
        return this.classroomRepo.getClassrooms(params);
    }

    @Override
    public Classroom getClassroomWithStudents(Integer id) {
        return this.classroomRepo.getClassroomWithStudents(id);
    }

    @Override
    public int countClassroom(Map<String, String> params) {
        return this.classroomRepo.countClassroom(params);
    }

    @Override
    public void removeStudentFromClassroom(int classroomId, int studentId) {
        this.classroomRepo.removeStudentFromClassroom(classroomId, studentId);
    }

    @Override
    public boolean existsDuplicateClassroom(String name, Integer semesterId, Integer courseId, Integer excludeId) {
        return this.classroomRepo.existsDuplicateClassroom(name, semesterId, courseId, excludeId);
    }

    @Override
    public boolean existsStudentInOtherClassroom(int studentId, int semesterId, int courseId, Integer excludeClassroomId) {
        return this.classroomRepo.existsStudentInOtherClassroom(
                studentId, semesterId, courseId, excludeClassroomId
        );
    }

    @Override
    public boolean existUserInClassroom(int userId, int classRoomId) {
        return this.classroomRepo.existUserInClassroom(userId, classRoomId);
    }
    
    @Override
    public void lockClassroomGrades(Integer classroomId) {
        Classroom classroom = this.getClassroomWithStudents(classroomId);
        Set<Student> students = classroom.getStudentSet();

        if (students.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không có sinh viên trong lớp này.");
        }

        for (Student student : students) {
            Map<String, Integer> params = new HashMap<>();
            params.put("classroomId", classroomId);
            params.put("studentId", student.getId());
            List<GradeDetail> gradeDetails = gradeDetailService.getGradeDetail(params);
            if (gradeDetails == null) {
                return;
            }
            GradeDetail gradeDetail = gradeDetails.get(0);
            if (gradeDetail == null
                    || gradeDetail.getMidtermGrade() == null
                    || gradeDetail.getFinalGrade() == null) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chưa nhập đầy đủ điểm cho tất cả sinh viên.");
            }

            if (gradeDetail.getExtraGradeSet() != null) {
                for (ExtraGrade extra : gradeDetail.getExtraGradeSet()) {
                    if (extra.getGrade() == null) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chưa nhập đầy đủ điểm cho tất cả sinh viên.");
                    }
                }
            }
        }

        classroom.setGradeStatus("LOCKED");
        this.saveClassroom(classroom);

        // Gửi email thông báo bất đồng bộ cho từng sinh viên
        for (Student student : students) {
            User user = student.getUser();
            if (user != null && user.getEmail() != null) {
                String subject = "Thông báo khóa điểm lớp " + classroom.getName();
                String body = String.format("Chào %s %s,\n\nBảng điểm lớp %s đã được công bố.\nVui lòng kiểm tra điểm của bạn trên hệ thống.",
                        user.getLastName(), user.getFirstName(), classroom.getName());
                mailUtils.sendEmailAsync(user.getEmail(), subject, body);
            }
        }
    }

}
