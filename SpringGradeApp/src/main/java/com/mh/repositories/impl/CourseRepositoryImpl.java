/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.repositories.impl;

import com.mh.pojo.Course;
import com.mh.repositories.CourseRepository;
import com.mh.utils.PageSize;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
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
public class CourseRepositoryImpl implements CourseRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Course> getCourses(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Course> cq = cb.createQuery(Course.class);
        Root<Course> root = cq.from(Course.class);

        List<Predicate> predicates = new ArrayList<>();

        if (params != null) {
            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                Predicate namePredicate = cb.like(root.get("name"), "%" + kw + "%");
                predicates.add(namePredicate);
            }
        }

        cq.where(predicates.toArray(new Predicate[0]));

        Query query = session.createQuery(cq);

        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.get("page"));
            int start = (page - 1) * PageSize.COURSE_PAGE_SIZE.getSize();
            query.setMaxResults(PageSize.COURSE_PAGE_SIZE.getSize());
            query.setFirstResult(start);
        }

        return query.getResultList();
    }

    @Override
    public Course saveCourse(Course course) {
        Session session = factory.getObject().getCurrentSession();

        if (course.getId() == null || course.getId() == 0) {
            session.persist(course);
        } else {
            session.merge(course);
        }

        return course;
    }

    @Override
    public Course getCourseById(int id) {
        Session session = factory.getObject().getCurrentSession();
        return session.get(Course.class, id);
    }

    @Override
    public void deleteCourseById(int id) {
        Session session = factory.getObject().getCurrentSession();
        Course course = session.get(Course.class, id);

        if (course != null) {
            session.remove(course);
        }
    }

    @Override
    public int countCourse(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Course> root = cq.from(Course.class);

        cq.select(cb.count(root));

        String kw = params.get("kw");
        if (kw != null && !kw.isEmpty()) {
            Predicate predicate = cb.like(root.get("name"), "%" + kw + "%");
            cq.where(predicate);
        }

        Long result = session.createQuery(cq).getSingleResult();
        return result.intValue();
    }

}
