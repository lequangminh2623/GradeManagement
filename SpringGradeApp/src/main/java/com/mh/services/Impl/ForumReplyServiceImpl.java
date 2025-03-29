/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.services.Impl;
import com.mh.pojo.ForumReply;
import com.mh.repositories.ForumReplyRepository;
import com.mh.services.ForumReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 *
 * @author Le Quang Minh
 */


@Service
public class ForumReplyServiceImpl implements ForumReplyService {
    
    @Autowired
    private ForumReplyRepository forumReplyRepo;

    @Override
    public List<ForumReply> getForumRepliesByForumPostId(int forumPostId) {
        return forumReplyRepo.getForumRepliesByForumPostId(forumPostId);
    }
}

