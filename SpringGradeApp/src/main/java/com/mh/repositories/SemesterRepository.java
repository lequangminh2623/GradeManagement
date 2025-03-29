package com.mh.repositories;

import com.mh.pojo.Semester;
import java.util.List;

/**
 *
 * @author Le Quang Minh
 */
public interface SemesterRepository {
    List<Semester> getSemestersByAcademicYearName(String year);
}
