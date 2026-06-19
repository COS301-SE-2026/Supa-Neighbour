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
     * Constructs an Comments with all fields specified.
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

    /**
     * Gets the comment identifier.
     *
     * @return the comment identifier
     */
    public Integer getCommentid(){
        return commentid;
    }

    /**
     * Sets the comment identifier.
     *
     * @param commentid the comment identifier
     */
    public void setCommentid(Integer commentid){
        this.commentid = commentid;
    }

    /**
     * Gets the user who wrote the comment.
     *
     * @return the user
     */
    public User getUserid(){
        return userid;
    }

    /**
     * Sets the user who wrote the comment.
     *
     * @param userid the user
     */
    public void setUserid(User userid){
        this.userid = userid;
    }

    /**
     * Gets the post the comment belongs to.
     *
     * @return the post
     */
    public Posts getPostid(){
        return postid;
    }

    /**
     * Sets the post the comment belongs to.
     *
     * @param postid the post
     */
    public void setPostid(Posts postid){
        this.postid = postid;
    }

    /**
     * Gets the parent comment identifier for nested comments.
     *
     * @return the parent comment identifier
     */
    public Integer getParentCommentid(){
        return parentCommentid;
    }

    /**
     * Sets the parent comment identifier for nested comments.
     *
     * @param parentCommentid the parent comment identifier
     */
    public void setParentCommentid(Integer parentCommentid){
        this.parentCommentid = parentCommentid;
    }

    /**
     * Gets the content of the comment.
     *
     * @return the comment content
     */
    public String getCommentContent(){
        return commentContent;
    }

    /**
     * Sets the content of the comment.
     *
     * @param commentContent the comment content
     */
    public void setCommentContent(String commentContent){
        this.commentContent = commentContent;
    }

    /**
     * Gets the timestamp when the comment was created.
     *
     * @return the creation timestamp
     */

    public Timestamp getCreatedAt(){
        return createdAt;
    }

    /**
     * Sets the timestamp when the comment was created.
     *
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the timestamp when the comment was last updated.
     *
     * @return the last updated timestamp
     */
    public Timestamp getUpdatedAt(){
        return updatedAt;
    }

    /**
     * Sets the timestamp when the comment was last updated.
     *
     * @param updatedAt the last updated timestamp
     */

    public void setUpdatedAt(Timestamp updatedAt){
        this.updatedAt = updatedAt;
    }



}
