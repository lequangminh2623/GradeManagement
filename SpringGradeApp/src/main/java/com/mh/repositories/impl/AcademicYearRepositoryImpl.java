/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.repositories.impl;

import com.mh.pojo.AcademicYear;
import com.mh.pojo.AcademicYear;
import com.mh.repositories.AcademicYearRepository;
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
public class AcademicYearRepositoryImpl implements AcademicYearRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    public List<AcademicYear> getAcademicYears(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<AcademicYear> cq = cb.createQuery(AcademicYear.class);
        Root<AcademicYear> root = cq.from(AcademicYear.class);
        cq.select(root);

        List<Predicate> predicates = new ArrayList<>();

        if (params != null) {
            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                Predicate namePredicate = cb.like(root.get("year"), "%" + kw + "%");
                predicates.add(namePredicate);
            }
            cq.where(predicates.toArray(new Predicate[0]));
        }

        Query query = session.createQuery(cq);

        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.get("page"));
            int start = (page - 1) * PageSize.YEAR_PAGE_SIZE.getSize();
            query.setMaxResults(PageSize.YEAR_PAGE_SIZE.getSize());
            query.setFirstResult(start);
        }

        return query.getResultList();
    }

    @Override
    public int countYears(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AcademicYear> root = cq.from(AcademicYear.class);

        cq.select(cb.count(root));

        String kw = params.get("kw");
        if (kw != null && !kw.isEmpty()) {
            Predicate predicate = cb.like(root.get("year"), "%" + kw + "%");
            cq.where(predicate);
        }

        Long result = session.createQuery(cq).getSingleResult();
        return result.intValue();
    }

    @Override
    public AcademicYear saveYear(AcademicYear year) {
        Session session = this.factory.getObject().getCurrentSession();

        if (year.getId() == null || year.getId() == 0) {
            session.persist(year);
        } else {
            session.merge(year);
        }

        return year;
    }

    @Override
    public AcademicYear getYearById(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(AcademicYear.class, id);
    }

    @Override
    public void deleteYearById(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        AcademicYear year = session.get(AcademicYear.class, id);
        if (year != null) {
            session.remove(year);
        }
    }

    @Override
    public boolean existAcademicYearByYear(String year, Integer excludeId) {
        Session session = factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<AcademicYear> cq = cb.createQuery(AcademicYear.class);
        Root<AcademicYear> root = cq.from(AcademicYear.class);
        cq.select(root);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("year"), year));

        if (excludeId != null) {
            predicates.add(cb.notEqual(root.get("id"), excludeId));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        Query query = session.createQuery(cq);

        List<AcademicYear> result = query.getResultList();
        AcademicYear course = result.isEmpty() ? null : result.get(0);

        return course != null ? true : false;
    }

}
