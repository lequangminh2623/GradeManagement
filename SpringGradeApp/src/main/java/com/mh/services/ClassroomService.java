/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mh.services;

import com.mh.pojo.Classroom;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Le Quang Minh
 */
public interface ClassroomService {

    List<Classroom> getClassrooms(Map<String, String> params);

    Classroom saveClassroom(Classroom classroom, List<Integer> studentIds);

    Classroom getClassroomById(Integer id);

    void deleteClassroom(Integer id);
    
    Classroom getClassroomWithStudents(Integer id);
    
    int countClassroom(Map<String, String> params);
}
