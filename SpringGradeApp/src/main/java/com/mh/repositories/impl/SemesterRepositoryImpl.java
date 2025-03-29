package com.mh.repositories.impl;

import com.mh.pojo.Semester;
import com.mh.repositories.SemesterRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
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
public class SemesterRepositoryImpl implements SemesterRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Semester> getSemestersByAcademicYearName(String year) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Semester> q = cb.createQuery(Semester.class);
        Root<Semester> root = q.from(Semester.class);
        q.select(root);

        if (year != null && !year.isEmpty()) {
            Predicate predicate = cb.equal(root.get("academicYear").get("year"), year);
            q.where(predicate);
        }

        Query query = session.createQuery(q);
        return query.getResultList();
    }
}
