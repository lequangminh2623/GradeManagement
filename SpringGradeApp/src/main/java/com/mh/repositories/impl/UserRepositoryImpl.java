/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.repositories.impl;

import com.mh.pojo.User;
import com.mh.repositories.UserRepository;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Le Quang Minh
 */
@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {

    private static final int PAGE_SIZE = 15;

    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User getUserByEmail(String email) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createNamedQuery("User.findByEmail", User.class);
        q.setParameter("email", email);

        List<User> users = q.getResultList();
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public boolean authenticate(String email, String password) {
        User u = this.getUserByEmail(email);
        if (u == null) {
            return false;
        }
        return this.passwordEncoder.matches(password, u.getPassword());
    }

    @Override
    public List<User> getUsers(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
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
    public User getUserById(Integer id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(User.class, id);
    }

    @Override
    public void deleteUser(Integer id) {
        Session s = this.factory.getObject().getCurrentSession();
        User u = this.getUserById(id);
        if (u != null) {
            s.remove(u);
        }
    }

    @Override
    public User saveUser(User user) {
        Session session = this.factory.getObject().getCurrentSession();

        if (user.getId() == null || user.getId() == 0) {
            session.persist(user);
        } else {
            session.merge(user);
        }
        return user;
    }

    @Override
    public int countUser(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<User> root = cq.from(User.class);

        cq.select(cb.count(root));

        List<Predicate> predicates = new ArrayList<>();

        String kw = params.get("kw");
        if (kw != null && !kw.isEmpty()) {
            Predicate firstNameLike = cb.like(cb.lower(root.get("firstName")), "%" + kw.toLowerCase() + "%");
            Predicate lastNameLike = cb.like(cb.lower(root.get("lastName")), "%" + kw.toLowerCase() + "%");
            predicates.add(cb.or(firstNameLike, lastNameLike));
            cq.where(predicates.toArray(new Predicate[0]));
        }

        Long result = session.createQuery(cq).getSingleResult();
        return result.intValue();
    }

    @Override
    public List<User> getUserByRole(List<String> roles) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);

        if (roles != null && !roles.isEmpty()) {
            Predicate rolePredicate = root.get("role").in(roles);
            cq.where(rolePredicate);
        }

        cq.select(root);

        Query query = s.createQuery(cq);
        return query.getResultList();
    }

    @Override
    public boolean existsByEmail(String email, Integer excludeId) {
        Session session = factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<User> root = cq.from(User.class);

        Predicate pEmail = cb.equal(root.get("email"), email);
        Predicate pExclude = (excludeId == null)
                ? cb.conjunction()
                : cb.notEqual(root.get("id"), excludeId);

        cq.select(cb.count(root))
                .where(cb.and(pEmail, pExclude));

        Long count = session.createQuery(cq).getSingleResult();
        return count != null && count > 0;
    }
}
