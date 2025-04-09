/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.repositories.impl;

import com.mh.pojo.Student;
import com.mh.pojo.User;
import com.mh.repositories.StudentRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Le Quang Minh
 */
@Repository
@Transactional
public class StudentRepositoryImpl implements StudentRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Student> getStudentByUsername(String username) {
        Session session = factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Student> cq = cb.createQuery(Student.class);
        Root<Student> root = cq.from(Student.class);
        cq.select(root);

        if (username != null && !username.isEmpty()) {
            Join<Student, User> userJoin = root.join("user"); // Liên kết bảng User với Student
            cq.where(cb.equal(userJoin.get("name"), username));
        }

        Query query = session.createQuery(cq);
        return query.getResultList();
    }

    @Override
    public Student saveStudent(Student student) {
        Session session = this.factory.getObject().getCurrentSession();

        if (student.getId() == null || student.getId() == 0) {
            session.persist(student);
        } else {
            session.merge(student);
        }

        return student;
    }

    @Override
    public void deleteStudentByUserId(int userId) {
        Session session = this.factory.getObject().getCurrentSession();
        Student student = session.get(Student.class, userId);
        if (student != null) {
            session.remove(student);
        }
    }

}
