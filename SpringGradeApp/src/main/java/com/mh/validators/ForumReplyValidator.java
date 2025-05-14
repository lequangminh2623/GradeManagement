/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.validators;

import com.mh.pojo.ForumPost;
import com.mh.pojo.ForumReply;
import com.mh.services.ClassroomService;
import com.mh.services.ForumPostService;
import com.mh.services.ForumReplyService;
import java.util.List;
import java.util.Set;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * @author leoma
 */
@Component
public class ForumReplyValidator implements Validator {

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private ForumPostService forumPostService;

    @Autowired
    private ForumReplyService forumReplyService;

    @Override
    public boolean supports(Class<?> clazz) {
        return ForumReply.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ForumReply reply = (ForumReply) target;

        if (reply.getContent() == null || reply.getContent().trim().isEmpty()) {
            errors.rejectValue("content", "forumReply.content.notNull");
        }

        if (reply.getUser() == null || reply.getUser().getId() == null) {
            errors.rejectValue("user", "forumReply.user.notNull");
        }

        if (reply.getForumPost() == null || reply.getForumPost().getId() == null) {
            errors.rejectValue("forumPost", "forumReply.forumPost.notNull");
        }

        if (!errors.hasFieldErrors()) {
            ForumPost post = this.forumPostService.getForumPostById(reply.getForumPost().getId());
            if (post != null) {
                boolean existUserInClassroom = classroomService.existUserInClassroom(reply.getUser().getId(),
                        post.getClassroom().getId());

                if (!existUserInClassroom) {
                    errors.rejectValue("user", "forumReply.notInClassroom");
                    errors.rejectValue("forumPost", "forumReply.notInClassroom");
                }

                if (reply.getParent() != null && reply.getParent().getId() != null) {
                    List<ForumReply> replies = this.forumReplyService.getForumRepliesByForumPostId(post.getId(), null);

                    if (!replies.contains(reply.getParent())) {
                        errors.rejectValue("forumPost", "forumReply.notInPost");
                        errors.rejectValue("parent", "forumReply.notInPost");
                    }
                }

            } else {
                errors.rejectValue("forumPost", "forumReply.forumPost.invalid");
            }
        }
    }
}
