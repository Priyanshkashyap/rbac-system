package com.example.demo.controller;

import com.example.demo.dto.RoleConflictRequest;
import com.example.demo.entity.RoleConflict;
import com.example.demo.service.RoleConflictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role-conflicts")
public class RoleConflictController {

    @Autowired
    private RoleConflictService roleConflictService;

    @GetMapping
    public List<RoleConflict> getAll() {
        return roleConflictService.getAll();
    }

    @PostMapping
    public RoleConflict create(@RequestBody RoleConflictRequest request) {
        return roleConflictService.create(request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        roleConflictService.delete(id);
    }
}