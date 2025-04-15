package com.mh.repositories.impl;

import com.mh.pojo.Semester;
import com.mh.repositories.SemesterRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
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
public class SemesterRepositoryImpl implements SemesterRepository {
    
    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Override
    public List<Semester> getSemestersByAcademicYearId(int yearId, Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<Semester> cq = cb.createQuery(Semester.class);
        Root<Semester> root = cq.from(Semester.class);
        cq.select(root);
        
        List<Predicate> predicates = new ArrayList<>();
        
        predicates.add(cb.equal(root.get("academicYear").get("id"), yearId));
        
        if (params != null) {
            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                Predicate namePredicate = cb.like(root.get("semesterType"), "%" + kw + "%");
                predicates.add(namePredicate);
            }
        }
        
        cq.where(predicates.toArray(new Predicate[0]));
        
        Query query = s.createQuery(cq);
        return query.getResultList();
    }
    
    @Override
    public List<Semester> getSemesters(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<Semester> cq = cb.createQuery(Semester.class);
        Root<Semester> root = cq.from(Semester.class);
        cq.select(root);
        
        if (params != null) {
            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                Expression<String> combined = cb.concat(root.get("academicYear").get("name"), " - ");
                combined = cb.concat(combined, root.get("name"));
                
                Predicate likeCombined = cb.like(cb.lower(combined), "%" + kw.toLowerCase() + "%");
                cq.where(likeCombined);
            }
        }
        
        Query query = s.createQuery(cq);
        return query.getResultList();
    }
    
    @Override
    public Semester saveSemester(Semester semester) {
        Session session = this.factory.getObject().getCurrentSession();
        
        if (semester.getId() == null || semester.getId() == 0) {
            session.persist(semester);
        } else {
            session.merge(semester);
        }
        
        return semester;
    }
    
    @Override
    public Semester getSemesterById(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(Semester.class, id);
    }
    
    @Override
    public void deleteSemesterById(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        Semester semester = session.get(Semester.class, id);
        
        if (semester != null) {
            session.remove(semester);
        }
    }
    
}
