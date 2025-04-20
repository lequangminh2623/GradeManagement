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
import java.util.Map;

/**
 *
 * @author Le Quang Minh
 */
@Service
public class ForumReplyServiceImpl implements ForumReplyService {
    
    @Autowired
    private ForumReplyRepository forumReplyRepo;
    
    @Override
    public List<ForumReply> getForumRepliesByForumPostId(int forumPostId, Map<String, String> params) {
        return forumReplyRepo.getForumRepliesByForumPostId(forumPostId, params);
    }
    
    @Override
    public List<ForumReply> getForumRepliesByForumPostIdAndForumReplyId(int forumPostId, int parentId) {
        return this.forumReplyRepo.getForumRepliesByForumPostIdAndForumReplyId(forumPostId, parentId);
    }
    
    @Override
    public int countForumRepliesByForumPostId(int forumPostId, Map<String, String> params) {
        return this.forumReplyRepo.countForumRepliesByForumPostId(forumPostId, params);
    }
    
    @Override
    public List<ForumReply> getAllForumReplys(Map<String, String> params) {
        return this.forumReplyRepo.getAllForumReplys(params);
    }
    
    @Override
    public int countForumReplies(Map<String, String> params) {
        return this.forumReplyRepo.countForumReplies(params);
    }
    
    @Override
    public ForumReply getForumReplyById(int id) {
        return this.forumReplyRepo.getForumReplyById(id);
    }
    
    @Override
    public ForumReply saveForumReply(ForumReply forumReply) {
        return this.forumReplyRepo.saveForumReply(forumReply);
    }
    
    @Override
    public void deleteReplyById(int id) {
        this.forumReplyRepo.deleteReplyById(id);
    }
    
}
