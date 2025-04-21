/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.services.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.mh.pojo.ForumPost;
import com.mh.pojo.ForumReply;
import com.mh.repositories.ForumReplyRepository;
import com.mh.services.ForumReplyService;
import java.io.IOException;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Le Quang Minh
 */
@Service
public class ForumReplyServiceImpl implements ForumReplyService {

    @Autowired
    private ForumReplyRepository forumReplyRepo;

    @Autowired
    private Cloudinary cloudinary;

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
        if (forumReply.getCreatedDate() == null) {
            forumReply.setCreatedDate(new Date());
        }

        if (!forumReply.getFile().isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(forumReply.getFile().getBytes(),
                        ObjectUtils.asMap("resource_type", "auto", "folder", "GradeManagement"));
                forumReply.setImage(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(ForumPost.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        forumReply.setUpdatedDate(new Date());
        return this.forumReplyRepo.saveForumReply(forumReply);
    }

    @Override
    public void deleteReplyById(int id) {
        this.forumReplyRepo.deleteReplyById(id);
    }

}
