package com.app.api.services;

import com.app.api.models.AdminApplication;
import com.app.api.models.User;
import com.app.api.repositories.AdminApplicationRepository;
import com.app.api.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.time.LocalDate;
import java.util.List;
/**
 * AdminApplicationService
 */
@Service
public class AdminApplicationService {

    private static final String STATUS_PENDING = "Pending";
    private static final String STATUS_APPROVED ="Approved";
    private static final String STATUS_REJECTED ="Rejected";

    private final AdminApplicationRepository adminApplicationRepository;
    private final UserRepository userRepository;

    public AdminApplicationService(AdminApplicationRepository adminApplicationRepository,UserRepository userRepository) {
        this.adminApplicationRepository = adminApplicationRepository;
        this.userRepository = userRepository;
    }

    @Transactional 
    public AdminApplication createApplication(Integer userId, String justification) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
 
        AdminApplication application = new AdminApplication(user, justification);
        return adminApplicationRepository.save(application);
    }


    public AdminApplication getApplicationById(Integer applicationId) {
        return adminApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Admin application not found with id: " + applicationId));
    }
}
