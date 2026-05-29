package com.app.api.models;
import java.security.Timestamp;
import java.sql.Date;
import java.sql.Time;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import kotlin.time.TimeSource;
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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User userid;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name ="post_id")
    private Posts postid;

    @ManyToOne(cascade = CascadeType.ALL)
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

