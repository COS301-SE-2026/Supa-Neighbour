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

/**
 * Represents a comment on a post in the community forum.
 * Comments can be nested and include timestamps for tracking creation and updates.
 */
@Data
@Builder
@Entity
@Table(name = "comments_table")
public class Comments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer commentid;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User userid;

    @ManyToOne()
    @JoinColumn(name ="post_id")
    private Posts postid;

    @Column(name = "parent_comment_id")
    private Integer parentCommentid;

    @Column(name = "comment_content")
    private String commentContent; 
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    /**
     * Default constructor.
     */
    public Comments() {
    }

    /**
     * Constructs an Admin with all fields specified.
     *
     * @param commentid         the comment identifier
     * @param userid            the user password
     * @param postid            the post identifier
     * @param parentCommentid   the parent comment identifier
     * @param createdAt         the timestamp of the creation of the comment
     * @param updatedAt         the timestamp of the updated comment
     */
    public Comments(Integer commentid,User userid,Posts postid,Integer parentCommentid,String commentContent,Timestamp createdAt,Timestamp updatedAt) {
        this.commentid=commentid;
        this.postid = postid;
        this.userid= userid;
        this.parentCommentid= parentCommentid;
        this.commentContent = commentContent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
