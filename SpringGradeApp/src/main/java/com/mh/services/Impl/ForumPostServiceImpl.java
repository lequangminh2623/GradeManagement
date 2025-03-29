/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.services.Impl;
import com.mh.pojo.ForumPost;
import com.mh.repositories.ForumPostRepository;
import com.mh.services.ForumPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 *
 * @author Le Quang Minh
 */


@Service
public class ForumPostServiceImpl implements ForumPostService {
    
    @Autowired
    private ForumPostRepository forumPostRepo;

    @Override
    public List<ForumPost> getForumPostsByClassroomId(int classRoomId) {
        return forumPostRepo.getForumPostsByClassroomId(classRoomId);
    }
}
