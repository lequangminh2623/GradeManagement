/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mh.repositories;

import com.mh.pojo.Classroom;
import com.mh.pojo.Student;
import com.mh.pojo.User;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Le Quang Minh
 */
public interface ClassroomRepository {

    List<Classroom> getClassrooms(Map<String, String> params);

    Classroom saveClassroom(Classroom classroom);

    Classroom getClassroomById(Integer id);

    void deleteClassroom(Integer id);

    Classroom getClassroomWithStudents(Integer id);

    int countClassroom(Map<String, String> params);

    void removeStudentFromClassroom(int classroomId, int studentId);

    boolean existsDuplicateClassroom(String name, Integer semesterId, Integer courseId, Integer excludeId);

    boolean existsStudentInOtherClassroom(int studentId, int semesterId, int courseId, Integer excludeClassroomId);

    boolean existUserInClassroom(int userId, int classRoomId);

    List<Classroom> getClassroomsByUser(User user, Map<String, String> params);

    int countClassroomsByUser(User user, Map<String, String> params);

    Classroom getClassroomByForumPostId(int id);

    List<Student> getStudentsInClassroom(Integer classroomId, Map<String, String> params);
}
