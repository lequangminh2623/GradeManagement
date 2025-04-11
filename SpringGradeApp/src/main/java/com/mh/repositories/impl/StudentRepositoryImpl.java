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
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    public List<Student> getStudents(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<Student> cq = cb.createQuery(Student.class);
        Root<Student> root = cq.from(Student.class);
        Join<Student, User> userJoin = root.join("user");

        cq.select(root);

        List<Predicate> predicates = new ArrayList<>();

        if (params != null) {
            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                String pattern = "%" + kw.toLowerCase() + "%";

                Predicate firstNameLike = cb.like(cb.lower(userJoin.get("firstName")), pattern);
                Predicate lastNameLike = cb.like(cb.lower(userJoin.get("lastName")), pattern);
                Predicate codeLike = cb.like(cb.lower(root.get("code")), pattern);

                predicates.add(cb.or(firstNameLike, lastNameLike, codeLike));
            }

            String role = params.get("role");
            if (role != null && !role.isEmpty()) {
                predicates.add(cb.equal(userJoin.get("role"), role));
            }

            cq.where(predicates.toArray(new Predicate[0]));
        }

        Query query = s.createQuery(cq);
        return query.getResultList();
    }

    @Override
    public Student getStudentByUserId(int userId) {
        Session s = this.factory.getObject().getCurrentSession();

        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<Student> cq = cb.createQuery(Student.class);
        Root<Student> root = cq.from(Student.class);

        root.fetch("user");

        // Điều kiện: user.id = userId
        cq.select(root).where(cb.equal(root.get("user").get("id"), userId));

        Query q = s.createQuery(cq);
        List<Student> results = q.getResultList();

        return results.isEmpty() ? null : results.get(0);
    }

}
