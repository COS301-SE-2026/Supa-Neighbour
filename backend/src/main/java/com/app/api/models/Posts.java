package com.app.api.models;
import java.sql.Timestamp;

import jakarta.persistence.CascadeType;
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
 * Represents a post in the community forum.
 * Users can create posts with text content and media attachments.
 */
@Data
@Builder
@Entity
@Table(name = "posts_table")
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private int postid;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User userid;

    @Column(name = "post_content")
    private String postContent;
    @Column(name = "media_url")
    private String mediaURL;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    /**
     * Default constructor.
     */
    public Posts() {
    }

    /**
     * Constructs a Posts with all the fields specified
     *
     * @param postid              the posts identifier
     * @param userid              the user identifier of who posted it
     * @param postContent         the content of the post
     * @param mediaURL            the mediaURL
     * @param createdAt           the timestamp of creation of the post
     * @param updatedAt           the timestamp of when the post was updated
     */
    public Posts(int postid, User userid, String postContent,String mediaURL, Timestamp createdAt, Timestamp updatedAt) {
        this.postid=postid;
        this.userid= userid;
        this.postContent = postContent;
        this.mediaURL = mediaURL;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Gets the posts identifier.
     *
     * @return the posts identifier
     */
    public int getPostid() {
        return postid;
    }

    /**
     * Sets the post identifier.
     *
     * @param postid the post identifier
     */
    public void setPostid(int postid) {
        this.postid = postid;
    }

    /**
     * Gets the user identifier.
     *
     * @return the user identifier
     */
    public User getUserid() {
        return userid;
    }

    /**
    * Sets the user idenfitifer
    *
    * @param userid the user identifier
    */
    public void setUserid(User userid) {
        this.userid = userid;
    }


    /**
     * Gets the post content
     *
     * @return the post content.
     */
    public String getPostContent() {
        return postContent;
    }

    /**
    * Sets the post content
    *
    * @param postContent the post content
    */
    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    /**
     * Gets the media URL
     *
     * @return the media URL
     */
    public String getMediaURL() {
        return mediaURL;
    }

    /**
    * Sets the media URL
    *
    * @param mediaURL the mediaURL
    */
    public void setMediaURL(String mediaURL) {
        this.mediaURL = mediaURL;
    }

    /**
    * Gets the timestamp of the post's creation
    *
    * @return createdAt
    */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when the post was created.
     *
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the timestamp when the post was last updated.
     *
     * @return the last updated timestamp
     */
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the timestamp when the post was last updated.
     *
     * @param updatedAt the last updated timestamp
     */
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

}
