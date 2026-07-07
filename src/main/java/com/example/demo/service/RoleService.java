package com.example.demo.service;

import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PermissionRepository permissionRepository;

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }
    public Role assignPermission(
            Long roleId,
            Long permissionId
    ) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() ->
                        new RuntimeException("Role not found"));

        Permission permission =
                permissionRepository.findById(permissionId)
                        .orElseThrow(() ->
                                new RuntimeException("Permission not found"));

        role.getPermissions().add(permission);

        return roleRepository.save(role);//Oh, the permissions collection changed.So it automatically updates the join table
    }
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRole(Long id) {
        return roleRepository.findById(id)
                .orElseThrow();
    }

    public Role updateRole(
            Long id,
            Role updatedRole
    ) {

        Role role =
                roleRepository.findById(id)
                        .orElseThrow();

        role.setName(
                updatedRole.getName()
        );

        return roleRepository.save(role);
    }

    public void deleteRole(
            Long id
    ) {
        roleRepository.deleteById(id);
    }
}