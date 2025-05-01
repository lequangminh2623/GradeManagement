/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.validators;

import com.mh.pojo.ForumReply;
import com.mh.pojo.dto.ForumReplyDTO;
import com.mh.services.ClassroomService;
import com.mh.services.ForumPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * @author leoma
 */
@Component
public class ForumReplyDTOValidator implements Validator {

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private ForumPostService forumPostService;

    @Override
    public boolean supports(Class<?> clazz) {
        return ForumReplyDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ForumReplyDTO reply = (ForumReplyDTO) target;

        if (reply.getContent() == null || reply.getContent().trim().isEmpty()) {
            errors.rejectValue("content", "forumReply.content.notNull");
        }

    }
}
