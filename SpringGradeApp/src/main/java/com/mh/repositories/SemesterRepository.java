package com.mh.repositories;

import com.mh.pojo.Semester;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Le Quang Minh
 */
public interface SemesterRepository {
    List<Semester> getSemestersByAcademicYearName(String year);
    
    List<Semester> getSemesters(Map<String, String> params);
}
