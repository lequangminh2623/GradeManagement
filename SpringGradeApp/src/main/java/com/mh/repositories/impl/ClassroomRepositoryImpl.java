/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.repositories.impl;

import com.mh.pojo.Classroom;
import com.mh.pojo.ForumPost;
import com.mh.pojo.Student;
import com.mh.pojo.User;
import com.mh.repositories.ClassroomRepository;
import com.mh.utils.PageSize;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
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
                Predicate namePredicate = cb.like(root.get("name"), "%" + kw + "%");
                predicates.add(namePredicate);
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
            int start = (page - 1) * PageSize.CLASSROOM_PAGE_SIZE.getSize();
            query.setMaxResults(PageSize.CLASSROOM_PAGE_SIZE.getSize());
            query.setFirstResult(start);
        }

        return query.getResultList();
    }

    @Override
    public Classroom saveClassroom(Classroom classroom) {
        Session session = this.factory.getObject().getCurrentSession();
        Classroom persistentClassroom;

        if (classroom.getId() != null && classroom.getId() != 0) {
            persistentClassroom = session.get(Classroom.class, classroom.getId());

            persistentClassroom.setName(classroom.getName());
            persistentClassroom.setCourse(classroom.getCourse());
            persistentClassroom.setSemester(classroom.getSemester());
            persistentClassroom.setLecturer(classroom.getLecturer());
            persistentClassroom.setGradeStatus(classroom.getGradeStatus());

            persistentClassroom.setStudentSet(classroom.getStudentSet());

            session.merge(persistentClassroom);
        } else {
            session.persist(classroom);
            persistentClassroom = classroom;
        }

        return persistentClassroom;
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

    @Override
    public int countClassroom(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Classroom> root = cq.from(Classroom.class);

        cq.select(cb.count(root));

        String kw = params.get("kw");
        if (kw != null && !kw.isEmpty()) {
            Predicate predicate = cb.like(root.get("name"), "%" + kw + "%");
            cq.where(predicate);
        }

        Long result = session.createQuery(cq).getSingleResult();
        return result.intValue();
    }

    @Override
    public void removeStudentFromClassroom(int classroomId, int studentId) {
        Session session = this.factory.getObject().getCurrentSession();

        Classroom classroom = session.get(Classroom.class, classroomId);
        Student student = session.get(Student.class, studentId);

        if (classroom == null || student == null) {
            throw new RuntimeException("Classroom or student not found");
        }

        if (classroom.getStudentSet().contains(student)) {
            classroom.getStudentSet().remove(student);
            session.merge(classroom);
        } else {
            throw new RuntimeException("Student not in classroom");
        }
    }

    @Override
    public boolean existsDuplicateClassroom(String name, Integer semesterId, Integer courseId, Integer excludeId) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Classroom> root = cq.from(Classroom.class);

        cq.select(cb.count(root));

        Predicate predicate = cb.and(
                cb.equal(root.get("name"), name),
                cb.equal(root.get("semester").get("id"), semesterId),
                cb.equal(root.get("course").get("id"), courseId)
        );

        if (excludeId != null) {
            predicate = cb.and(predicate, cb.notEqual(root.get("id"), excludeId));
        }

        cq.where(predicate);

        Long count = session.createQuery(cq).getSingleResult();

        return count > 0;
    }

    @Override
    public boolean existsStudentInOtherClassroom(int studentId, int semesterId, int courseId, Integer excludeClassroomId) {
        Session session = this.factory.getObject().getCurrentSession();

        String hql = """
            SELECT COUNT(*)
            FROM Classroom c
            JOIN c.studentSet s
            WHERE s.id = :studentId
              AND c.semester.id = :semesterId
              AND c.course.id = :courseId
              AND (:excludeId IS NULL OR c.id <> :excludeId)
        """;

        Long count = session.createQuery(hql, Long.class)
                .setParameter("studentId", studentId)
                .setParameter("semesterId", semesterId)
                .setParameter("courseId", courseId)
                .setParameter("excludeId", excludeClassroomId)
                .uniqueResult();

        return count > 0;

    }

    @Override
    public boolean existUserInClassroom(int userId, int classRoomId) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Classroom> root = cq.from(Classroom.class);

        Join<Object, Object> studentJoin = root.join("studentSet", JoinType.LEFT);

        Predicate classroomMatch = cb.equal(root.get("id"), classRoomId);
        Predicate isStudent = cb.equal(studentJoin.get("id"), userId);
        Predicate isLecturer = cb.equal(root.get("lecturer").get("id"), userId);

        cq.select(cb.count(root)).where(cb.and(classroomMatch, cb.or(isStudent, isLecturer)));

        Long count = session.createQuery(cq).getSingleResult();

        return count != null && count > 0;
    }

    @Override
    public List<Classroom> getClassroomsByUser(User user, Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<Classroom> cq = cb.createQuery(Classroom.class);
        Root<Classroom> root = cq.from(Classroom.class);
        cq.select(root);

        List<Predicate> predicates = new ArrayList<>();
        if (user.getRole().equals("ROLE_LECTURER")) {
            predicates.add(cb.equal(root.get("lecturer").get("id"), user.getId()));
        } else if (user.getRole().equals("ROLE_STUDENT")) {
            Join<Object, Object> studentJoin = root.join("studentSet", JoinType.INNER);
            Predicate isStudent = cb.equal(studentJoin.get("id"), user.getId());
            predicates.add(isStudent);
        }

        if (params != null) {
            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                Predicate namePredicate = cb.like(root.get("name"), "%" + kw + "%");
                predicates.add(namePredicate);
            }
        }

        cq.where(predicates.toArray(new Predicate[0]));

        Query query = s.createQuery(cq);

        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.get("page"));
            int start = (page - 1) * PageSize.CLASSROOM_PAGE_SIZE.getSize();
            query.setMaxResults(PageSize.CLASSROOM_PAGE_SIZE.getSize());
            query.setFirstResult(start);
        }

        return query.getResultList();
    }

    @Override
    public Classroom getClassroomByForumPostId(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<Classroom> cq = cb.createQuery(Classroom.class);
        Root<ForumPost> root = cq.from(ForumPost.class);

        cq.select(root.get("classroom"))
                .where(cb.equal(root.get("id"), id));

        TypedQuery<Classroom> typedQuery = s.createQuery(cq);
        return typedQuery.getSingleResult();
    }

    @Override
    public List<Student> getStudentsInClassroom(Integer classroomId, Map<String, String> params) {
        if (classroomId == null) {
            throw new IllegalArgumentException("Thiếu tham số classroomId");
        }

        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Student> cq = cb.createQuery(Student.class);
        Root<Student> root = cq.from(Student.class);

        Join<Student, Classroom> classJoin = root.join("classroomSet", JoinType.INNER);
        Join<Student, User> userJoin = root.join("user", JoinType.INNER);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(classJoin.get("id"), classroomId));

        if (params != null) {
            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                String pattern = "%" + kw.toLowerCase() + "%";
                Expression<String> lastNameLower = cb.lower(userJoin.get("lastName"));
                Expression<String> firstNameLower = cb.lower(userJoin.get("firstName"));
                Expression<String> fullNameExpr = cb.concat(
                        cb.concat(lastNameLower, " "),
                        firstNameLower
                );
                Predicate codeLike = cb.like(cb.lower(root.get("code")), pattern);
                Predicate fullNameLike = cb.like(fullNameExpr, pattern);
                predicates.add(cb.or(codeLike, fullNameLike));
            }
        }

        cq.select(root)
                .distinct(true)
                .where((Expression<Boolean>) cb.and(predicates.toArray(new Predicate[0])));

        if (params != null) {
            String sortBy = params.get("sortBy");
            if (sortBy != null && !sortBy.isEmpty()) {
                cq.orderBy(cb.asc(root.get(sortBy)));
            }
        }

        Query query = session.createQuery(cq);

        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.get("page"));
            int pageSize = PageSize.USER_PAGE_SIZE.getSize();
            int start = (page - 1) * pageSize;
            query.setFirstResult(start);
            query.setMaxResults(pageSize);
        }

        return query.getResultList();
    }

}
