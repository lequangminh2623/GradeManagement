package com.mh.repositories.impl;

import com.mh.pojo.GradeDetail;
import com.mh.repositories.GradeDetailRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
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
public class GradeDetailRepositoryImpl implements GradeDetailRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<GradeDetail> getGradeDetailsByStudentIdAndSubjectId(Integer studentId, Integer subjectId) {
        Session session = factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<GradeDetail> cq = cb.createQuery(GradeDetail.class);
        Root<GradeDetail> root = cq.from(GradeDetail.class);
        cq.select(root);

        List<Predicate> predicates = new ArrayList<>();

        if (studentId != null) {
            predicates.add(cb.equal(root.get("student").get("id"), studentId));
        }

        if (subjectId != null) {
            predicates.add(cb.equal(root.get("course").get("id"), subjectId));
        }

        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        Query query = session.createQuery(cq);
        return query.getResultList();
    }
}
