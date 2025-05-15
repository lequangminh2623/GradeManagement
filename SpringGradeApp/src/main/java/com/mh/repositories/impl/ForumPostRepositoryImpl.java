package com.mh.repositories.impl;

import com.mh.pojo.ForumPost;
import com.mh.repositories.ForumPostRepository;
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
public class ForumPostRepositoryImpl implements ForumPostRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<ForumPost> getForumPosts(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ForumPost> cq = cb.createQuery(ForumPost.class);
        Root<ForumPost> root = cq.from(ForumPost.class);
        cq.select(root);

        List<Predicate> predicates = new ArrayList<>();

        if (params != null) {
            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                Predicate namePredicate = cb.like(root.get("title"), "%" + kw + "%");
                predicates.add(namePredicate);
            }

            String user = params.get("user");
            if (user != null && !user.isEmpty()) {
                predicates.add(cb.equal(root.get("user").get("id"), Integer.parseInt(user)));
            }

            String classroom = params.get("classroom");
            if (classroom != null && !classroom.isEmpty()) {
                predicates.add(cb.equal(root.get("classroom").get("id"), Integer.parseInt(classroom)));
            }

            cq.where(predicates.toArray(new Predicate[0]));

        }

        cq.orderBy(cb.desc(root.get("id")));

        Query query = session.createQuery(cq);

        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.get("page"));
            int start = (page - 1) * PageSize.FORUM_POST_PAGE_SIZE.getSize();
            query.setMaxResults(PageSize.FORUM_POST_PAGE_SIZE.getSize());
            query.setFirstResult(start);
        }

        return query.getResultList();
    }

    @Override
    public int countForumPosts(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ForumPost> root = cq.from(ForumPost.class);

        cq.select(cb.count(root));

        List<Predicate> predicates = new ArrayList<>();

        if (params != null) {
            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                Predicate namePredicate = cb.like(root.get("title"), "%" + kw + "%");
                predicates.add(namePredicate);
            }

            String classroom = params.get("classroom");
            if (classroom != null && !classroom.isEmpty()) {
                predicates.add(cb.equal(root.get("classroom").get("id"), Integer.parseInt(classroom)));
            }

            cq.where(predicates.toArray(new Predicate[0]));

        }

        Long result = session.createQuery(cq).getSingleResult();
        return result.intValue();
    }

    @Override
    public ForumPost getForumPostById(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(ForumPost.class, id);
    }

    @Override
    public ForumPost saveForumPost(ForumPost forumPost) {
        Session session = this.factory.getObject().getCurrentSession();

        if (forumPost.getId() == null || forumPost.getId() == 0) {
            session.persist(forumPost);
        } else {
            session.merge(forumPost);
        }
        return forumPost;
    }

    @Override
    public void deleteForumPostById(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        ForumPost forumPost = session.get(ForumPost.class, id);

        if (forumPost != null) {
            session.remove(forumPost);
        }
    }

}
