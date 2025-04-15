package com.mh.repositories;

import com.mh.pojo.Semester;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Le Quang Minh
 */
public interface SemesterRepository {

    List<Semester> getSemestersByAcademicYearId(int id, Map<String, String> params);

    List<Semester> getSemesters(Map<String, String> params);

    Semester saveSemester(Semester semester);

    Semester getSemesterById(int id);

    void deleteSemesterById(int id);
}
