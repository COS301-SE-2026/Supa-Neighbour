package com.app.api.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.api.models.User;

/**
 * Repository for User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    /**
     * Finds a user by their email address.
     *
     * @param email the email address of the user
     * @return an {@code Optional} containing the matching user if found;
     *         otherwise an empty {@code Optional}
     */
    Optional<User> findByEmail(String email);
    /**
     * Finds a user by their Firebase unique identifier.
     *
     * @param firebaseUid the Firebase UID of the user
     * @return an {@code Optional} containing the matching user if found;
     *         otherwise an empty {@code Optional}
     */
    Optional<User> findByFirebaseUid(String firebaseUid);

}
