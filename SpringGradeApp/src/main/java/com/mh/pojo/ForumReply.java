/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Le Quang Minh
 */
@Entity
@Table(name = "forum_reply")
@NamedQueries({
    @NamedQuery(name = "ForumReply.findAll", query = "SELECT f FROM ForumReply f"),
    @NamedQuery(name = "ForumReply.findById", query = "SELECT f FROM ForumReply f WHERE f.id = :id"),
    @NamedQuery(name = "ForumReply.findByImage", query = "SELECT f FROM ForumReply f WHERE f.image = :image"),
    @NamedQuery(name = "ForumReply.findByCreatedDate", query = "SELECT f FROM ForumReply f WHERE f.createdDate = :createdDate"),
    @NamedQuery(name = "ForumReply.findByUpdatedDate", query = "SELECT f FROM ForumReply f WHERE f.updatedDate = :updatedDate")})
public class ForumReply implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Column(name = "content")
    private String content;
    @Size(max = 255)
    @Column(name = "image")
    private String image;
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date createdDate;
    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date updatedDate;
    @JoinColumn(name = "forum_post_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    @JsonIgnore
    private ForumPost forumPost;
    @OneToMany(mappedBy = "parent")
    @JsonIgnore
    private Set<ForumReply> forumReplySet;
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @ManyToOne
    @JsonIgnore
    private ForumReply parent;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User user;
    @Transient
    @JsonIgnore
    private MultipartFile file;

    public ForumReply() {
    }

    public ForumReply(Integer id) {
        this.id = id;
    }

    public ForumReply(Integer id, String content) {
        this.id = id;
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public ForumPost getForumPost() {
        return forumPost;
    }

    public void setForumPost(ForumPost forumPost) {
        this.forumPost = forumPost;
    }

    public Set<ForumReply> getForumReplySet() {
        return forumReplySet;
    }

    public void setForumReplySet(Set<ForumReply> forumReplySet) {
        this.forumReplySet = forumReplySet;
    }

    public ForumReply getParent() {
        return parent;
    }

    public void setParent(ForumReply parent) {
        this.parent = parent;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ForumReply)) {
            return false;
        }
        ForumReply other = (ForumReply) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mh.pojo.ForumReply[ id=" + id + " ]";
    }

    /**
     * @return the file
     */
    public MultipartFile getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(MultipartFile file) {
        this.file = file;
    }

}
