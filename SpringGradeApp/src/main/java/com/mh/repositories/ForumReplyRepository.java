package com.mh.repositories;

import com.mh.pojo.ForumReply;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Le Quang Minh
 */
public interface ForumReplyRepository {

    List<ForumReply> getForumRepliesByForumPostId(int forumPostId, Map<String, String> params);

    List<ForumReply> getForumRepliesByForumPostIdAndForumReplyId(int forumPostId, int parentId);

    int countForumRepliesByForumPostId(int forumPostId, Map<String, String> params);

    List<ForumReply> getAllForumReplys(Map<String, String> params);

    int countForumReplies(Map<String, String> params);

    ForumReply getForumReplyById(int id);

    ForumReply saveForumReply(ForumReply forumReply);

    void deleteReplyById(int id);
}
