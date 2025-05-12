package com.mh.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mh.pojo.ForumPost;
import com.mh.pojo.ForumReply;
import com.mh.pojo.User;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author leoma
 */
public class ForumPostDetailDTO {

    private int id;
    private String title;
    private String content;
    private String image;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date createdDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date updatedDate;
    private User user;
    private Set<ForumReply> forumReplies;

    public ForumPostDetailDTO(ForumPost forumPost) {
        this.id = forumPost.getId();
        this.title = forumPost.getTitle();
        this.content = forumPost.getContent();
        this.image = forumPost.getImage();
        this.createdDate = forumPost.getCreatedDate();
        this.updatedDate = forumPost.getUpdatedDate();
        this.user = forumPost.getUser();
        this.forumReplies = forumPost.getForumReplySet();
    }

    public ForumPostDetailDTO() {
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the image
     */
    public String getImage() {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * @return the createdDate
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate the createdDate to set
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * @return the updatedDate
     */
    public Date getUpdatedDate() {
        return updatedDate;
    }

    /**
     * @param updatedDate the updatedDate to set
     */
    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the forumReplies
     */
    public Set<ForumReply> getForumReplies() {
        return forumReplies;
    }

    /**
     * @param forumReplies the forumReplies to set
     */
    public void setForumReplies(Set<ForumReply> forumReplies) {
        this.forumReplies = forumReplies;
    }

}
