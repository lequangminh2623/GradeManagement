/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mh.services;

import com.mh.pojo.ForumReply;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Le Quang Minh
 */
public interface ForumReplyService {

    List<ForumReply> getForumRepliesByForumPostId(int forumPostId, Map<String, String> params);

    List<ForumReply> getForumRepliesByForumPostIdAndForumReplyId(int forumPostId, int parentId, Map<String, String> params);

    int countForumRepliesByForumPostId(int forumPostId, Map<String, String> params);

    List<ForumReply> getAllForumReplys(Map<String, String> params);

    int countForumReplies(Map<String, String> params);

    ForumReply getForumReplyById(int id);

    ForumReply saveForumReply(ForumReply forumReply);

    void deleteReplyById(int id);

    boolean isReplyStillEditable(int forumReplyId);

    boolean checkOwnerForumReplyPermission(int userId, int replyId);

}
