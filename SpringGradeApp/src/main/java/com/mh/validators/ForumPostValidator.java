/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.validators;

import com.mh.pojo.ForumPost;
import com.mh.services.ClassroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * @author leoma
 */
@Component
public class ForumPostValidator implements Validator {

    @Autowired
    private ClassroomService classroomService;

    @Override
    public boolean supports(Class<?> clazz) {
        return ForumPost.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ForumPost post = (ForumPost) target;

        if (post.getTitle() == null || post.getTitle().trim().isEmpty()) {
            errors.rejectValue("title", "forumPost.title.notNull");
        }

        if (post.getClassroom() == null || post.getClassroom().getId() == null) {
            errors.rejectValue("classroom", "forumPost.classroom.notNull");
        }

        if (post.getUser() == null || post.getUser().getId() == null) {
            errors.rejectValue("user", "forumPost.user.notNull");
        }

        if (!errors.hasFieldErrors()) {
            boolean existUserInClassroom = classroomService.existUserInClassroom(post.getUser().getId(), post.getClassroom().getId());

            if (!existUserInClassroom) {
                errors.rejectValue("user", "forumPost.notInClassroom");
                errors.rejectValue("classroom", "forumPost.notInClassroom");
            }

        }
    }

}
