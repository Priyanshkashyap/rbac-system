package com.example.demo.service;

import com.example.demo.entity.AuditLog;
import com.example.demo.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository repository;

    public void log(
            String action,
            String username
    ) {

        AuditLog log = new AuditLog();

        log.setAction(action);
        log.setUsername(username);
        log.setTimestamp(LocalDateTime.now());

        repository.save(log);
    }
    public List<AuditLog> getLogsByUser(
            String username
    ) {
        return repository.findByUsername(
                username
        );
    }

    public List<AuditLog> getLogsByAction(
            String action
    ) {
        return repository.findByAction(
                action
        );
    }
}
