package org.example.ebookstore.security;

import org.example.ebookstore.entities.AuditLog;
import org.example.ebookstore.repositories.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LoginSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {
    private final AuditLogRepository auditLogRepository;

    @Autowired
    public LoginSuccessListener(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            AuditLog log = new AuditLog();
            log.setUserId(userDetails.getId());
            log.setTimestamp(LocalDateTime.now());
            log.setActionType("LOGIN_SUCCESS");
            log.setDetails("Login successful for user: " + userDetails.getUsername());
            auditLogRepository.save(log);
        }
    }
}
