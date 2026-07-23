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
 * Represents a like action on posts or comments in the community forum.
 * Tracks user engagement with content through likes.
 */
@Data
@Builder
@Entity
@Table(name = "reaction_table")
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reaction_id")
    private int reactionid;

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

    @Column(name = "reaction_type")
    private String reactionType;
    

    /**
     * Default constructor.
     */
    public Reaction() {
    }

    /**
     * Constructs a Likes record with all fields specified.
     *
     * @param reactionid     the like identifier
     * @param userid     the user who performed the like action
     * @param postid     the post that was liked
     * @param commentid  the comment that was liked
     * @param createdAt  the timestamp when the like was created
     * @param updatedAt  the timestamp when the like was last updated
     */
    public Reaction(int reactionid,User userid,Posts postid,Comments commentid,Timestamp createdAt,Timestamp updatedAt, String reactionType) {
        this.reactionid = reactionid;
        this.commentid=commentid;
        this.postid = postid;
        this.userid= userid;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.reactionType = reactionType;
    }


    /**
     * Gets the reaction type
     *
     * @return the reaction type
     */
    public String getReactionType() { 
        return reactionType; 
    }

    /**
     * Sets the reaction type
     *
     * @param reactionType the like type
     */
    public void setReactionType(String reactionType) { 
        this.reactionType = reactionType; 
    }

    /**
     * Gets the like identifier.
     *
     * @return the like identifier
     */
    public int getreactionid(){
        return reactionid;
    }
   
    /**
     * Gets the user who performed the like action.
     *
     * @return the user
     */
    public User getUserid(){
        return userid;
    }

    /**
     * Gets the post that was liked.
     *
     * @return the post
     */
    public Posts getPostid(){
        return postid;
    }

    /**
     * Gets the comment that was liked.
     *
     * @return the comment
     */
    public Comments getCommentid(){
        return commentid;
    }

    /**
     * Gets the timestamp when the like was created.
     *
     * @return the creation timestamp
     */
    public Timestamp getCreatedAt(){
        return createdAt;
    }

    /**
     * Gets the timestamp when the like was last updated.
     *
     * @return the last updated timestamp
     */
    public Timestamp getUpdatedAt(){
        return updatedAt;
    }

    /**
     * Sets the like identifier.
     *
     * @param reactionid the like identifier
     */
    public void setReactionid(int reactionid){
        this.reactionid=reactionid;
    }

    /**
     * Sets the user who performed the like action.
     *
     * @param userid the user
     */
    public void setUserid(User userid){
        this.userid=userid;
    }

    /**
     * Sets the comment that was liked.
     *
     * @param commentid the comment
     */
    public void setCommentid(Comments commentid){
        this.commentid= commentid;
    }

    /**
     * Sets the post that was liked.
     *
     * @param postid the post
     */
    public void setPostid(Posts postid){
        this.postid = postid;
    }

    /**
     * Sets the timestamp when the like was created.
     *
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(Timestamp createdAt){
        this.createdAt = createdAt;
    }

    /**
     * Sets the timestamp when the like was last updated.
     *
     * @param updatedAt the last updated timestamp
     */
    public void setUpdatedAt(Timestamp updatedAt){
        this.updatedAt=updatedAt;
    }
}
