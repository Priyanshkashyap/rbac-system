package com.example.demo.controller;

import com.example.demo.entity.Role;
import com.example.demo.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    @PostMapping
    public Role createRole(@RequestBody Role role) {
        return roleService.createRole(role);
    }

    @PutMapping("/{roleId}/permissions/{permissionId}")
    public Role assignPermission(
            @PathVariable Long roleId,
            @PathVariable Long permissionId
    ) {
        return roleService.assignPermission(roleId, permissionId);
    }

    @PutMapping("/{roleId}/parent/{parentRoleId}")
    public Role assignParentRole(
            @PathVariable Long roleId,
            @PathVariable Long parentRoleId
    ) {
        return roleService.assignParentRole(roleId, parentRoleId);
    }

    @DeleteMapping("/{roleId}/parent")
    public Role removeParentRole(@PathVariable Long roleId) {
        return roleService.removeParentRole(roleId);
    }

    @GetMapping("/{id}")
    public Role getRole(@PathVariable Long id) {
        return roleService.getRole(id);
    }

    @DeleteMapping("/{id}")
    public void deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
    }

    @PutMapping("/{id}")
    public Role updateRole(
            @PathVariable Long id,
            @RequestBody Role role
    ) {
        return roleService.updateRole(id, role);
    }
}