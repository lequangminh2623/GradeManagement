package com.mh.repositories;

import com.mh.pojo.ForumPost;
import java.util.List;

/**
 *
 * @author Le Quang Minh
 */
public interface ForumPostRepository {
    List<ForumPost> getForumPostsByClassroomId(int classRoomId);
}
