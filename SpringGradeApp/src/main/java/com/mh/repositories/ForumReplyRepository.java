package com.mh.repositories;

import com.mh.pojo.ForumReply;
import java.util.List;

/**
 *
 * @author Le Quang Minh
 */
public interface ForumReplyRepository {
    List<ForumReply> getForumRepliesByForumPostId(int forumPostId);
}
