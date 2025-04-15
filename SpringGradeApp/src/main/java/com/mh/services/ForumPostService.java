/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mh.services;

import com.mh.pojo.ForumPost;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Le Quang Minh
 */
public interface ForumPostService {

    List<ForumPost> getForumPosts(Map<String, String> params);

    int countForumPosts(Map<String, String> params);

    ForumPost getForumPostById(int id);

    ForumPost saveForumPost(ForumPost forumPost);

    void deleteForumPostById(int id);

}
