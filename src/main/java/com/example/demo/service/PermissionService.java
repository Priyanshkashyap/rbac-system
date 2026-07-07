package com.example.demo.service;

import com.example.demo.entity.Permission;
import com.example.demo.repository.PermissionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository repository;

    public Permission create(
            Permission permission
    ) {
        return repository.save(permission);
    }

    public List<Permission> getAll() {
        return repository.findAll();
    }

    public void delete(
            Long id
    ) {
        repository.deleteById(id);
    }
}