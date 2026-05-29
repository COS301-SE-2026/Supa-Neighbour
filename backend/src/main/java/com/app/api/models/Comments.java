package com.app.api.models;
import java.security.Timestamp;
import java.sql.Date;

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
@Table(name = "comments_table")
public class Comments {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comments_id_seq")
    @Column(name = "comment_id")
    private int commentid;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    @Column(name = "user_id")
    private User userid;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name ="post_id")
    @Column(name = "post_id")
    private Posts postid;

    @Column(name = "parent_comment_id")
    private int parentCommentid;

    @Column(name = "comment_content")
    private String commentContent; 
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public Comments() {
    }

    public Comments(int commentid,User userid,Posts postid,int parentCommentid,String commentContent,Timestamp createdAt,Timestamp updatedAt) {
        this.commentid=commentid;
        this.postid = postid;
        this.userid= userid;
        this.parentCommentid= parentCommentid;
        this.commentContent = commentContent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    
}

