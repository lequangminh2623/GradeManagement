/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.repositories.impl;

import com.mh.pojo.Classroom;
import com.mh.pojo.Classroom;
import com.mh.pojo.Student;
import com.mh.repositories.ClassroomRepository;
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
public class ClassroomRepositoryImpl implements ClassroomRepository {

    private static final int PAGE_SIZE = 15;

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Classroom> getClassrooms(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<Classroom> cq = cb.createQuery(Classroom.class);
        Root<Classroom> root = cq.from(Classroom.class);
        cq.select(root);

        List<Predicate> predicates = new ArrayList<>();

        if (params != null) {
            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                Predicate firstNameLike = cb.like(cb.lower(root.get("firstName")), "%" + kw.toLowerCase() + "%");
                Predicate lastNameLike = cb.like(cb.lower(root.get("lastName")), "%" + kw.toLowerCase() + "%");
                predicates.add(cb.or(firstNameLike, lastNameLike));
            }

            String role = params.get("role");
            if (role != null && !role.isEmpty()) {
                predicates.add(cb.equal(root.get("role"), role));
            }

            cq.where(predicates.toArray(new Predicate[0]));

            String sortBy = params.get("sortBy");
            if (sortBy != null && !sortBy.isEmpty()) {
                cq.orderBy(cb.asc(root.get(sortBy)));
            }
        }

        Query query = s.createQuery(cq);

        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.get("page"));
            int start = (page - 1) * PAGE_SIZE;
            query.setMaxResults(PAGE_SIZE);
            query.setFirstResult(start);
        }

        return query.getResultList();
    }

    @Override
    public Classroom saveClassroom(Classroom classroom) {
        Session session = this.factory.getObject().getCurrentSession();

        if (classroom.getId() == null || classroom.getId() == 0) {
            session.persist(classroom);
        } else {
            session.merge(classroom);
        }
        return classroom;
    }

    @Override
    public Classroom getClassroomById(Integer id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Classroom.class, id);
    }

    @Override
    public void deleteClassroom(Integer id) {
        Session s = this.factory.getObject().getCurrentSession();
        Classroom u = this.getClassroomById(id);
        if (u != null) {
            s.remove(u);
        }
    }

    @Override
    public Classroom getClassroomWithStudents(Integer id) {
        Session session = this.factory.getObject().getCurrentSession();

        String hql = "SELECT c FROM Classroom c "
                + "LEFT JOIN FETCH c.studentSet "
                + "LEFT JOIN FETCH c.course "
                + "LEFT JOIN FETCH c.lecturer "
                + "LEFT JOIN FETCH c.semester "
                + "WHERE c.id = :id";

        return session.createQuery(hql, Classroom.class)
                .setParameter("id", id)
                .uniqueResult();
    }
}
