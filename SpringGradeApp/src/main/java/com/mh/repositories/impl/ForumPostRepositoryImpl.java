package com.mh.repositories.impl;

import com.mh.pojo.ForumPost;
import com.mh.repositories.ForumPostRepository;
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
public class ForumPostRepositoryImpl implements ForumPostRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<ForumPost> getForumPostsByClassroomId(int classRoomId) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ForumPost> cq = cb.createQuery(ForumPost.class);
        Root<ForumPost> root = cq.from(ForumPost.class);
        cq.select(root);

        Predicate predicate = cb.equal(root.get("classroom").get("id"), classRoomId);
        cq.where(predicate);

        Query query = session.createQuery(cq);
        return query.getResultList();
    }
}
