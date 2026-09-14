package com.app.api.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.api.models.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer>{
    
}
