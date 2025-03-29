/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.services.Impl;
import com.mh.pojo.ExtraGrade;
import com.mh.repositories.ExtraGradeRepository;
import com.mh.services.ExtraGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 *
 * @author Le Quang Minh
 */


@Service
public class ExtraGradeServiceImpl implements ExtraGradeService {
    
    @Autowired
    private ExtraGradeRepository extraGradeRepo;

    @Override
    public List<ExtraGrade> getExtraGradesByGradeDetailId(int gradeDetailId) {
        return extraGradeRepo.getExtraGradesByGradeDetailId(gradeDetailId);
    }
}
