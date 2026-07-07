package com.example.demo.controller;

import com.example.demo.entity.AuditLog;
import com.example.demo.repository.AuditLogRepository;
import com.example.demo.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
//Because audit logs should be created automatically by the system, not manually by users. so only a get request and audit logs should be  created automatically
@RestController
@RequestMapping("/api/audit")
public class AuditLogController {

    @Autowired
    private AuditLogRepository repository;
    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    public List<AuditLog> getLogs() {
        return repository.findAll();
    }

    @GetMapping("/user/{username}")
    public List<AuditLog> getByUser(
            @PathVariable String username
    ) {
        return auditLogService
                .getLogsByUser(
                        username
                );
    }
    @GetMapping("/action/{action}")
    public List<AuditLog> getByAction(
            @PathVariable String action
    ) {
        return auditLogService
                .getLogsByAction(
                        action
                );
    }
}