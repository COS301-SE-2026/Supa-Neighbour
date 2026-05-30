package com.app.api.models;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name = "likes_table")
public class Likes {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "likes_id_seq")
    @Column(name = "like_id")
    private int likeid;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User userid;

    @ManyToOne()
    @JoinColumn(name ="post_id")
    private Posts postid;

    @ManyToOne()
    @JoinColumn(name = "comment_id")
    private Comments commentid;

    @Column(name = "created_at")
    private Timestamp createdAt;
    
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public Likes() {
    }

    public Likes(int likeid,User userid,Posts postid,Comments commentid,Timestamp createdAt,Timestamp updatedAt) {
        this.likeid = likeid;
        this.commentid=commentid;
        this.postid = postid;
        this.userid= userid;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    int getlikeid()
    {
        return likeid;
    }

    User getUserid()
    {
        return userid;
    }
    
    Posts gePostsid()
    {
        return postid;
    }
    
    Comments gCommentsid()
    {
        return commentid;
    }

    Timestamp getCreatedAt()
    {
        return createdAt;
    }

    Timestamp getUpdatedAt()
    {
        return updatedAt;
    }

    void setLikesid(int likeid)
    {
        this.likeid=likeid;
    }

    void setUserid(User userid)
    {
        this.userid=userid;
    }

    void setCommentsid(Comments commentid)
    {
        this.commentid= commentid;
    }

    void setPostid(Posts postid)
    {
        this.postid = postid;
    }

    void setCreatedAt(Timestamp createdAt)
    {
        this.createdAt = createdAt;
    }

    void setUpdatedAt(Timestamp updatedAt)
    {
        this.updatedAt=updatedAt;
    }
}

