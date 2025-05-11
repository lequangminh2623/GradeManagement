package com.mh.repositories.impl;

import com.mh.pojo.Classroom;
import com.mh.pojo.ExtraGrade;
import com.mh.pojo.GradeDetail;
import com.mh.repositories.ClassroomRepository;
import com.mh.repositories.GradeDetailRepository;
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
public class GradeDetailRepositoryImpl implements GradeDetailRepository {

    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Autowired
    private ClassroomRepository classroomRepo;

    @Override
    public List<GradeDetail> getGradeDetail(Map<String, Integer> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<GradeDetail> cq = cb.createQuery(GradeDetail.class);
        Root<GradeDetail> root = cq.from(GradeDetail.class);
        cq.select(root);

        List<Predicate> predicates = new ArrayList<>();

        if (params != null) {
            Integer studentId = params.get("studentId");
            if (studentId != null) {
                predicates.add(cb.equal(root.get("student").get("id"), studentId));
            }

            Integer courseId = params.get("courseId");
            if (courseId != null) {
                predicates.add(cb.equal(root.get("course").get("id"), courseId));
            }

            Integer semesterId = params.get("semesterId");
            if (semesterId != null) {
                predicates.add(cb.equal(root.get("semester").get("id"), semesterId));
            }

            Integer classroomId = params.get("classroomId");
            if (classroomId != null) {
                Classroom classroom = classroomRepo.getClassroomById(classroomId);
                predicates.add(cb.equal(root.get("course").get("id"), classroom.getCourse().getId()));
                predicates.add(cb.equal(root.get("semester").get("id"), classroom.getSemester().getId()));
            }

            cq.where(predicates.toArray(new Predicate[0]));
        }

        Query query = s.createQuery(cq);
        return query.getResultList();
    }

    @Override
    public void deleteGradeDetail(Integer id) {
        Session session = this.factory.getObject().getCurrentSession();
        GradeDetail gd = session.get(GradeDetail.class, id);
        if (gd != null) {
            session.remove(gd);
        }
    }

    @Override
    public GradeDetail saveGradeDetail(GradeDetail gd) {
        Session session = this.factory.getObject().getCurrentSession();

        if (gd.getExtraGradeSet() != null) {
            for (ExtraGrade eg : gd.getExtraGradeSet()) {
                eg.setGradeDetail(gd);
            }
        }

        if (gd.getId() == null || gd.getId() == 0) {
            session.persist(gd);
        } else {
            session.merge(gd);
        }

        return gd;
    }

    @Override
    public boolean existsByStudentAndCourseAndSemester(Integer studentId, Integer courseId, Integer semesterId, Integer excludeId) {
        Session session = factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<GradeDetail> root = cq.from(GradeDetail.class);

        Predicate p = cb.and(
                cb.equal(root.get("student").get("id"), studentId),
                cb.equal(root.get("course").get("id"), courseId),
                cb.equal(root.get("semester").get("id"), semesterId)
        );

        if (excludeId != null) {
            p = cb.and(p, cb.notEqual(root.get("id"), excludeId));
        }

        cq.select(cb.count(root)).where(p);
        Long count = session.createQuery(cq).getSingleResult();
        return count > 0;
    }

    @Override
    public boolean existsByGradeDetailIdAndGradeIndex(Integer gradeDetailId, Integer gradeIndex, Integer currentExtraGradeId) {
        Session session = factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<ExtraGrade> root = query.from(ExtraGrade.class);

        Predicate byGradeDetail = cb.equal(root.get("gradeDetail").get("id"), gradeDetailId);
        Predicate byIndex = cb.equal(root.get("gradeIndex"), gradeIndex);

        if (currentExtraGradeId != null) {
            Predicate notSameId = cb.notEqual(root.get("id"), currentExtraGradeId);
            query.where(cb.and(byGradeDetail, byIndex, notSameId));
        } else {
            query.where(cb.and(byGradeDetail, byIndex));
        }

        query.select(cb.count(root));

        Long count = session.createQuery(query).getSingleResult();
        return count > 0;
    }

    @Override
    public List<GradeDetail> getGradeDetailByStudent(Integer id) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<GradeDetail> cq = cb.createQuery(GradeDetail.class);
        Root<GradeDetail> root = cq.from(GradeDetail.class);

        cq.where(cb.equal(root.get("student").get("id"), id));
        cq.select(root);

        Query query = s.createQuery(cq);
        return query.getResultList();
    }

}
