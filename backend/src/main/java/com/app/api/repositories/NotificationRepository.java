package com.app.api.repositories;
import com.app.api.models.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notifications, Integer>{
    
    List<Notifications> findByUser_UseridOrderByCreatedatDesc(int userid);
}
