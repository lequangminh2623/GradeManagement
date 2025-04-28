/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.services.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.mh.pojo.Classroom;
import com.mh.pojo.ForumPost;
import com.mh.pojo.User;
import com.mh.repositories.ClassroomRepository;
import com.mh.repositories.ForumPostRepository;
import com.mh.services.ForumPostService;
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
public class ForumPostServiceImpl implements ForumPostService {

    @Autowired
    private ForumPostRepository forumPostRepo;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Override
    public List<ForumPost> getForumPosts(Map<String, String> params) {
        return this.forumPostRepo.getForumPosts(params);
    }

    @Override
    public int countForumPosts(Map<String, String> params) {
        return this.forumPostRepo.countForumPosts(params);
    }

    @Override
    public ForumPost getForumPostById(int id) {
        return this.forumPostRepo.getForumPostById(id);
    }

    @Override
    public ForumPost saveForumPost(ForumPost forumPost) {

        if (forumPost.getCreatedDate() == null) {
            forumPost.setCreatedDate(new Date());
        }

        if (forumPost.getFile() != null && !forumPost.getFile().isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(forumPost.getFile().getBytes(),
                        ObjectUtils.asMap("resource_type", "auto", "folder", "GradeManagement"));
                forumPost.setImage(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(ForumPost.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        forumPost.setUpdatedDate(new Date());
        return this.forumPostRepo.saveForumPost(forumPost);
    }

    @Override
    public void deleteForumPostById(int id) {
        this.forumPostRepo.deleteForumPostById(id);
    }

    @Override
    public boolean checkForumPostPermission(int userId, int classroomId) {
        return this.classroomRepository.existUserInClassroom(userId, classroomId);
    }

    @Override
    public boolean checkOwnerForumPostPermission(int userId, int forumPostId) {
        ForumPost forumPost = forumPostRepo.getForumPostById(forumPostId);
        return forumPost.getUser().getId() == userId;
    }

    @Override
    public boolean isPostStillEditable(int forumPostId) {
        ForumPost forumPost = forumPostRepo.getForumPostById(forumPostId);
        Date createdDate = forumPost.getCreatedDate();
        Date currentDate = new Date();

        long time = (currentDate.getTime() - createdDate.getTime()) / (60 * 1000);
        return time < 30;
    }

}
