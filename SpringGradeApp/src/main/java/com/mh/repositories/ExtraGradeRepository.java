package com.mh.repositories;

import com.mh.pojo.ExtraGrade;
import java.util.List;

/**
 *
 * @author Le Quang Minh
 */
public interface ExtraGradeRepository {
    List<ExtraGrade> getExtraGradesByGradeDetailId(int gradeDetailId);
}
