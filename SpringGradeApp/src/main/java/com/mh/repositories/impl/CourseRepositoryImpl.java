/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.repositories.impl;

import com.mh.pojo.Course;
import com.mh.repositories.CourseRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
public class CourseRepositoryImpl implements CourseRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
public List<Course> getCourses(Map<String, String> params) {
    Session s = this.factory.getObject().getCurrentSession();
    CriteriaBuilder cb = s.getCriteriaBuilder();
    CriteriaQuery<Course> cq = cb.createQuery(Course.class);
    Root<Course> root = cq.from(Course.class);
    cq.select(root);

    if (params != null) {
        String kw = params.get("kw");
        if (kw != null && !kw.isEmpty()) {
            Predicate nameLike = cb.like(cb.lower(root.get("name")), "%" + kw.toLowerCase() + "%");
            cq.where(nameLike);
        }
    }

    Query query = s.createQuery(cq);
    return query.getResultList();
}

}
