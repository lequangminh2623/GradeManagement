package com.mh.repositories.impl;

import com.mh.pojo.ForumReply;
import com.mh.repositories.ForumReplyRepository;
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
public class ForumReplyRepositoryImpl implements ForumReplyRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<ForumReply> getForumRepliesByForumPostId(int forumPostId) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ForumReply> cq = cb.createQuery(ForumReply.class);
        Root<ForumReply> root = cq.from(ForumReply.class);
        cq.select(root);

        Predicate predicate = cb.equal(root.get("forumPost").get("id"), forumPostId);
        cq.where(predicate);

        Query query = session.createQuery(cq);
        return query.getResultList();
    }
}
