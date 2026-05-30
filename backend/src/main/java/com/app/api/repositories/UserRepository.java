package com.app.api.repositories;
import java.util.*;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.api.models.User;
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
}
