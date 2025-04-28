package com.mh.repositories.impl;

import com.mh.pojo.ForumPost;
import com.mh.pojo.ForumReply;
import com.mh.repositories.ForumReplyRepository;
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
public class ForumReplyRepositoryImpl implements ForumReplyRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<ForumReply> getForumRepliesByForumPostId(int forumPostId, Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ForumReply> cq = cb.createQuery(ForumReply.class);
        Root<ForumReply> root = cq.from(ForumReply.class);
        cq.select(root);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("forumPost").get("id"), forumPostId));
        predicates.add(cb.equal(root.get("parent").get("id"), root.get("id")));

        if (params != null) {
            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                Predicate namePredicate = cb.like(root.get("content"), "%" + kw + "%");
                predicates.add(namePredicate);
            }

        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        cq.orderBy(cb.desc(root.get("createdDate")));

        Query query = session.createQuery(cq);

        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.get("page"));
            int start = (page - 1) * PageSize.FORUM_REPLY_PAGE_SIZE.getSize();
            query.setMaxResults(PageSize.FORUM_REPLY_PAGE_SIZE.getSize());
            query.setFirstResult(start);
        }

        return query.getResultList();
    }

    @Override
    public List<ForumReply> getForumRepliesByForumPostIdAndForumReplyId(int forumPostId, int parentId, Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ForumReply> cq = cb.createQuery(ForumReply.class);
        Root<ForumReply> root = cq.from(ForumReply.class);
        cq.select(root);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("forumPost").get("id"), forumPostId));
        predicates.add(cb.equal(root.get("parent").get("id"), parentId));
        predicates.add(cb.notEqual(root.get("parent").get("id"), root.get("id")));

        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        cq.orderBy(cb.desc(root.get("createdDate")));

        Query query = session.createQuery(cq);

        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.get("page"));
            int start = (page - 1) * PageSize.FORUM_REPLY_PAGE_SIZE.getSize();
            query.setMaxResults(PageSize.FORUM_REPLY_PAGE_SIZE.getSize());
            query.setFirstResult(start);
        }

        return query.getResultList();

    }

    @Override
    public int countForumRepliesByForumPostId(int forumPostId, Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ForumReply> root = cq.from(ForumReply.class);
        cq.select(cb.count(root));

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("forumPost").get("id"), forumPostId));
        predicates.add(cb.equal(root.get("parent").get("id"), root.get("id")));

        if (params != null) {
            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                Predicate namePredicate = cb.like(root.get("content"), "%" + kw + "%");
                predicates.add(namePredicate);
            }

            cq.where(cb.and(predicates.toArray(new Predicate[0])));

        }

        Long result = session.createQuery(cq).getSingleResult();

        return result.intValue();
    }

    @Override
    public List<ForumReply> getAllForumReplys(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ForumReply> cq = cb.createQuery(ForumReply.class);
        Root<ForumReply> root = cq.from(ForumReply.class);

        List<Predicate> predicates = new ArrayList<>();

        if (params != null) {
            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                Predicate namePredicate = cb.like(root.get("content"), "%" + kw + "%");
                predicates.add(namePredicate);
            }

            String user = params.get("user");
            if (user != null && !user.isEmpty()) {
                predicates.add(cb.equal(root.get("user").get("id"), Integer.parseInt(user)));
            }

        }

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.desc(root.get("createdDate")));

        Query query = session.createQuery(cq);

        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.get("page"));
            int start = (page - 1) * PageSize.FORUM_REPLY_PAGE_SIZE.getSize();
            query.setMaxResults(PageSize.FORUM_REPLY_PAGE_SIZE.getSize());
            query.setFirstResult(start);
        }

        return query.getResultList();
    }

    @Override
    public int countForumReplies(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ForumReply> root = cq.from(ForumReply.class);

        cq.select(cb.count(root));

        String kw = params.get("kw");
        if (kw != null && !kw.isEmpty()) {
            Predicate predicate = cb.like(root.get("content"), "%" + kw + "%");
            cq.where(predicate);
        }

        Long result = session.createQuery(cq).getSingleResult();
        return result.intValue();
    }

    @Override
    public ForumReply getForumReplyById(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(ForumReply.class, id);
    }

    @Override
    public ForumReply saveForumReply(ForumReply forumReply) {
        Session session = this.factory.getObject().getCurrentSession();

        if (forumReply.getId() == null || forumReply.getId() == 0) {
            session.persist(forumReply);
            session.refresh(forumReply);

            if (forumReply.getParent() == null) {
                forumReply.setParent(forumReply);
                session.merge(forumReply);
            }
        } else {
            session.merge(forumReply);
        }

        return forumReply;

    }

    @Override
    public void deleteReplyById(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        ForumReply reply = session.get(ForumReply.class, id);

        if (reply != null) {
            ForumPost post = reply.getForumPost();
            if (post != null) {
                post.getForumReplySet().remove(reply);
                session.remove(reply);
            }
        }
    }

}
