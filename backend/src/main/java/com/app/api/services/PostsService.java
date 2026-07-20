package com.app.api.services;

import java.sql.Timestamp;
import java.util.List;
 
import org.springframework.stereotype.Service;
 
import com.app.api.dtos.CreatePostRequest;
import com.app.api.dtos.PostFeedItemDTO;
import com.app.api.dtos.PostFeedResponseDTO;
import com.app.api.models.Posts;
import com.app.api.models.User;
import com.app.api.repositories.BulletinFeedRepository;
import com.app.api.repositories.PostsRepository;
import com.app.api.repositories.UserRepository;
import com.app.api.services.BlobStorageService;
import com.app.api.dtos.PostDetailDTO;


/**
 * Service layer for managing post operations.
 * Provides CRUD functionality for Posts entities.
 */
@Service
public class PostsService {

    private final PostsRepository postsRepository;
    private final UserRepository userRepository;
     private final BulletinFeedRepository bulletinFeedRepository;
     private final BlobStorageService blobStorageService;
    private static final List<String> VALID_CATEGORIES = List.of("general", "lost_pet", "local_event", "alert", "free_items", "complaint", "admin");

    private static final String EXPECTED_BLOB_HOST = "blob.core.windows.net";
    private static final String DEFAULT_CATEGORY = "general";
    /**
     * Constructs the repository with its required service dependency.
     *
     * @param postsRepository repository providing analytics data for posts
     */
    public PostsService(PostsRepository postsRepository, UserRepository userRepository,  BulletinFeedRepository bulletinFeedRepository, BlobStorageService blobStorageService) {
        this.postsRepository = postsRepository;
        this.userRepository = userRepository;
        this.bulletinFeedRepository = bulletinFeedRepository;
        this.blobStorageService = blobStorageService;
    }

    // Get all
    /**
     * Retrieves all posts from the repository.
     *
     * @return a list of all posts
     */
    public List<Posts> getAllPosts() {
        return postsRepository.findAll();
    }

    // Get by id
    /**
     * Retrieves a post by its identifier.
     *
     * @param id the post identifier
     * @return the post if found, or null if no post exists with the given id
     */
    public Posts getPostById(int id) {
        return postsRepository.findById(id).orElse(null);
    }

    /**
     * Validation result for a create-post request. Thrown as an exception
     * so the controller can translate it into the documented HTTP status.
     */
    public static class InvalidPostException extends RuntimeException{
        private final int statusCode;

        /**
         * Constructs a new {@code InvalidPostException}.
         *
         * @param message the error message describing why the request is invalid
         * @param statusCode the HTTP status code that should be returned
         *                   to the client
         */
        public InvalidPostException(String message, int statusCode){
            super(message);
            this.statusCode = statusCode;
        }

        /**
         * Returns the HTTP status code associated with this exception.
         *
         * @return the HTTP status code to return to the client
         */
        public int getStatusCode(){
            return statusCode;
        }
    }
    
    /**
     * Creates a new bulletin board post for the given authenticated user,
     * validating fields per the documented API contract (7.3):
     * - postContent is required
     * - mediaUrl, if supplied, must look like a Blob Storage URL
     * - category, if supplied, must be one of the known values; defaults to "general"
     *
     * @param user    the authenticated user creating the post (resolved from JWT, not client input)
     * @param request the validated request body
     * @return the saved post
     */
    public Posts createPost(int userId, CreatePostRequest request){
        User user = userRepository.findById(userId).orElse(null);
        if(user == null){
            throw new InvalidPostException("Unauthorized", 401);
        }
        if(request.getPostContent() == null || request.getPostContent().isBlank()){
            throw new InvalidPostException("postContent is required", 422);
        }
        String mediaUrl = request.getMediaUrl();

        if(mediaUrl != null && !mediaUrl.isBlank() && !mediaUrl.contains(EXPECTED_BLOB_HOST)){
            throw new InvalidPostException("mediaUrl must be a valid uploaded image URL", 400);
        }

        String category = request.getCategory();
        if(category == null || category.isBlank()){
            category = DEFAULT_CATEGORY;
        }else if(!VALID_CATEGORIES.contains(category)){
            throw new InvalidPostException("category must be one of: " + String.join(", ", VALID_CATEGORIES), 400);
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());

        Posts post = new Posts();
        post.setUserid(user);
        post.setPostContent(request.getPostContent());
        post.setMediaURL(mediaUrl);
        post.setCategory(category);
        post.setCreatedAt(now);
        post.setUpdatedAt(now);

        return postsRepository.save(post);
    }
    // Update
    /**
     * Updates an existing post with the provided details.
     *
     * @param id      the identifier of the post to update
     * @param updated the post object containing the updated fields
     * @return the updated post, or null if no post exists with the given id
     */
    public Posts updatePost(int id, Posts updated) {
        Posts existing = postsRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setUserid(updated.getUserid());
        existing.setPostContent(updated.getPostContent());
        existing.setCreatedAt(updated.getCreatedAt());
        existing.setUpdatedAt(updated.getUpdatedAt());
        existing.setMediaURL(updated.getMediaURL());
        existing.setCategory(updated.getCategory());

        return postsRepository.save(existing);
    }

    // Delete
    /**
     * Deletes a post by its identifier 
     *
     * @param postId the identifier of the post to delete
     * @param userId the authenticated user's id, resolved from the JWT
     *               (never trusted from client input)
     */
    public void deletePost(int postId, int userId) {
       Posts existing = postsRepository.findById(postId).orElse(null);
       if(existing == null){
            throw new InvalidPostException("Post not found", 404);
       }    

       if(existing.getUserid().getUserid() != userId){
        throw new InvalidPostException("You are not authorised to delete this post", 403);
       }

       postsRepository.deleteById(postId);
    }

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_LIMIT = 20;
     /**
     * Returns the bulletin board feed for the caller's neighbourhood zone (7.1),
     * with optional category filter, keyword search, and pagination.
     *
     * @param userId   the authenticated user's id, resolved from the JWT
     * @param category optional category filter, or null for no filter.
     *                 An unrecognised category simply yields no matches (not an error),
     *                 matching the endpoint's documented empty-state behaviour.
     * @param search   optional keyword search against post_content, or null for no filter
     * @param page     requested page number; defaults to 1 if null or less than 1
     * @param limit    requested page size; defaults to 20 if null or less than 1
     * @return the feed response envelope
     */
     public PostFeedResponseDTO getFeed(int userId, String category, String search, Integer page, Integer limit){
        int resolvedPage = (page == null || page < 1) ? DEFAULT_PAGE : page;
        int resolvedLimit = (limit == null || limit < 1) ? DEFAULT_LIMIT : limit;

        int offset = (resolvedPage - 1) * resolvedLimit;

        BulletinFeedRepository.CallerNeighbourhood neighbourhood = bulletinFeedRepository.findCallerNeighbourhood(userId);

        if(neighbourhood == null){
            return new PostFeedResponseDTO(null, resolvedPage, 0, List.of());
        }

        List<PostFeedItemDTO> posts = bulletinFeedRepository.findFeed(neighbourhood.locationId, category, search, resolvedLimit, offset);
        for(PostFeedItemDTO post: posts){
            if(post.getMediaUrl() != null){
                post.setMediaUrl(blobStorageService.generateSasUrl(post.getMediaUrl()));
            }
        }
        long totalPosts = bulletinFeedRepository.countFeed(neighbourhood.locationId, category, search);
        return new PostFeedResponseDTO(neighbourhood.neighbourhoodName, resolvedPage, totalPosts, posts);
     
    }


    /**
     * Returns the full detail view for a single post 
     *
     * @param postId the post's id
     * @return the post detail, or null if the post doesn't exist
     */
    public PostDetailDTO getPostDetail(int postId){
        PostDetailDTO detail = bulletinFeedRepository.findPostDetail(postId);
        if(detail != null && detail.getMediaUrl() != null){
            detail.setMediaUrl(blobStorageService.generateSasUrl(detail.getMediaUrl()));
        }
        return detail;
    }
}
