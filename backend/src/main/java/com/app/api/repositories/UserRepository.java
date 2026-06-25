package com.app.api.repositories;

import org.springframework.stereotype.Repository;
import java.lang.foreign.Linker.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.app.api.models.User;

/**
 * Repository for User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUid(String uid);
    Optional<User> findByEmail(String email);

}
