package com.example.demo.service;

import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.entity.RoleConflict;
import com.example.demo.entity.RoleGroup;
import com.example.demo.entity.User;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.RoleConflictRepository;
import com.example.demo.repository.RoleGroupRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleGroupRepository roleGroupRepository;
    @Autowired
    private RoleConflictRepository roleConflictRepository;
    @Autowired
    private RoleConflictService roleConflictService;

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    public Role assignPermission(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        role.getPermissions().add(permission);
        return roleRepository.save(role);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRole(Long id) {
        return roleRepository.findById(id).orElseThrow();
    }

    public Role updateRole(Long id, Role updatedRole) {
        Role role = roleRepository.findById(id).orElseThrow();
        role.setName(updatedRole.getName());
        return roleRepository.save(role);
    }

    @Transactional
    public Role assignParentRole(Long roleId, Long parentRoleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Role parentRole = roleRepository.findById(parentRoleId)
                .orElseThrow(() -> new RuntimeException("Parent role not found"));

        if (role.getId().equals(parentRole.getId())) {
            throw new RuntimeException("A role cannot be its own parent");
        }

        if (createsCycle(role, parentRole)) {
            throw new RuntimeException("Circular role hierarchy is not allowed");
        }

        if (roleConflictService.rolesConflict(role, parentRole)) {
            throw new RuntimeException("A role cannot inherit from a conflicting role");
        }

        role.setParentRole(parentRole);
        return roleRepository.save(role);
    }

    @Transactional
    public Role removeParentRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        role.setParentRole(null);
        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Remove role from every user
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getRoles().remove(role)) {
                userRepository.save(user);
            }
        }

        // Remove role from every role group
        List<RoleGroup> groups = roleGroupRepository.findAll();
        for (RoleGroup group : groups) {
            if (group.getRoles().remove(role)) {
                roleGroupRepository.save(group);
            }
        }

        // Detach child roles
        List<Role> allRoles = roleRepository.findAll();
        for (Role child : allRoles) {
            if (child.getParentRole() != null
                    && child.getParentRole().getId() != null
                    && child.getParentRole().getId().equals(id)) {
                child.setParentRole(null);
                roleRepository.save(child);
            }
        }

        // Remove conflict mappings
        List<RoleConflict> conflicts = roleConflictRepository.findByRoleOneIdOrRoleTwoId(id, id);
        for (RoleConflict conflict : conflicts) {
            roleConflictRepository.delete(conflict);
        }

        role.getPermissions().clear();
        roleRepository.save(role);
        roleRepository.delete(role);
    }

    private boolean createsCycle(Role child, Role candidateParent) {
        Role current = candidateParent.getParentRole();

        while (current != null) {
            if (current.getId() != null && current.getId().equals(child.getId())) {
                return true;
            }
            current = current.getParentRole();
        }

        return false;
    }
}