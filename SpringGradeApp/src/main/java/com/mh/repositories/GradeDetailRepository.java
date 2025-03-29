package com.mh.repositories;

import com.mh.pojo.GradeDetail;
import java.util.List;

/**
 *
 * @author Le Quang Minh
 */
public interface GradeDetailRepository {
    List<GradeDetail> getGradeDetailsByStudentIdAndSubjectId(Integer studentId, Integer subjectId);
}
