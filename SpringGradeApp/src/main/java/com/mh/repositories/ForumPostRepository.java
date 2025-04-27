package com.mh.repositories;

import com.mh.pojo.Classroom;
import com.mh.pojo.ForumPost;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Le Quang Minh
 */
public interface ForumPostRepository {

    List<ForumPost> getForumPosts(Map<String, String> params);

    int countForumPosts(Map<String, String> params);

    ForumPost getForumPostById(int id);

    ForumPost saveForumPost(ForumPost forumPost);

    void deleteForumPostById(int id);
}
