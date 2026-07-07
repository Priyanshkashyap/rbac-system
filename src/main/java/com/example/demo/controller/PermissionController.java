package com.example.demo.controller;

import com.example.demo.entity.Permission;
import com.example.demo.service.PermissionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @PostMapping
    public Permission create(
            @RequestBody Permission permission
    ) {
        return permissionService.create(permission);
    }

    @GetMapping
    public List<Permission> getAll() {
        return permissionService.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id
    ) {
        permissionService.delete(id);
    }
}