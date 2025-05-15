package com.mh.repositories;

import com.mh.pojo.GradeDetail;
import com.mh.pojo.dto.GradeDetailDTO;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Le Quang Minh
 */
public interface GradeDetailRepository {

    List<GradeDetail> getGradeDetail(Map<String, Integer> params);

    void deleteGradeDetail(Integer id);

    GradeDetail saveGradeDetail(GradeDetail gd);

    boolean existsByStudentAndCourseAndSemester(Integer studentId, Integer courseId, Integer semesterId, Integer excludeId);

    boolean existsByGradeDetailIdAndGradeIndex(Integer gradeDetailId, Integer gradeIndex, Integer currentExtraGradeId);

    List<GradeDetailDTO> getGradeDetailByStudent(Integer id, Map<String, String> params);
    
    List<GradeDetail> getGradeDetailsBySemester(Integer semesterId);
    
    List<GradeDetail> getGradeDetailsByLecturerAndSemester(Integer lecturerId, Integer semesterId);
}
