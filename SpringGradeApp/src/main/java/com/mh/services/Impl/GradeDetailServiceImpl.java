/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.services.Impl;
import com.mh.pojo.GradeDetail;
import com.mh.repositories.GradeDetailRepository;
import com.mh.services.GradeDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 *
 * @author Le Quang Minh
 */


@Service
public class GradeDetailServiceImpl implements GradeDetailService {
    
    @Autowired
    private GradeDetailRepository gradeDetailRepo;

    @Override
    public List<GradeDetail> getGradeDetailsByStudentIdAndSubjectId(Integer studentId, Integer subjectId) {
        return gradeDetailRepo.getGradeDetailsByStudentIdAndSubjectId(studentId, subjectId);
    }
}

