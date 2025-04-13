package com.mh.repositories;

import com.mh.pojo.GradeDetail;
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
}
