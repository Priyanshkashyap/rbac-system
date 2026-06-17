package com.example.demo.controller;

import com.example.demo.entity.RoleGroup;
import com.example.demo.service.RoleGroupService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role-groups")
public class RoleGroupController {

    @Autowired
    private RoleGroupService roleGroupService;

    @PostMapping
    public RoleGroup create(
            @RequestBody RoleGroup group
    ) {
        return roleGroupService.create(group);
    }

    @GetMapping
    public List<RoleGroup> getAll() {
        return roleGroupService.getAll();
    }

    @GetMapping("/{id}")
    public RoleGroup getById(
            @PathVariable Long id
    ) {
        return roleGroupService.getById(id);
    }

    @PutMapping("/{id}")
    public RoleGroup update(
            @PathVariable Long id,
            @RequestBody RoleGroup group
    ) {
        return roleGroupService.update(id, group);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id
    ) {
        roleGroupService.delete(id);
    }
    @PutMapping("/{groupId}/roles/{roleId}")
    public RoleGroup assignRole(
            @PathVariable Long groupId,
            @PathVariable Long roleId
    ) {
        return roleGroupService.assignRole(
                groupId,
                roleId
        );
    }
}