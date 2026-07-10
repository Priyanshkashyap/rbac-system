package com.example.demo.service;

import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.entity.RoleGroup;
import com.example.demo.entity.User;
import com.example.demo.repository.PermissionRepository;
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

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    public Role assignPermission(
            Long roleId,
            Long permissionId
    ) {

        Role role =
                roleRepository.findById(roleId)
                        .orElseThrow(() ->
                                new RuntimeException("Role not found"));

        Permission permission =
                permissionRepository.findById(permissionId)
                        .orElseThrow(() ->
                                new RuntimeException("Permission not found"));

        role.getPermissions().add(permission);

        return roleRepository.save(role);
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

        role.setName(updatedRole.getName());

        return roleRepository.save(role);

    }

    @Transactional // either everything or nothing
    public void deleteRole(Long id) {

        Role role =
                roleRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Role not found"));

        /*
            STEP 1
            Remove role from every user
         */

        List<User> users =
                userRepository.findAll();

        for (User user : users) {

            if (user.getRoles().remove(role)) {

                userRepository.save(user);

            }

        }

        /*
            STEP 2
            Remove role from every role group
         */

        List<RoleGroup> groups =
                roleGroupRepository.findAll();

        for (RoleGroup group : groups) {

            if (group.getRoles().remove(role)) {

                roleGroupRepository.save(group);

            }

        }

        /*
            STEP 3
            Remove every permission mapping
         */

        role.getPermissions().clear();

        roleRepository.save(role);

        /*
            STEP 4
            Delete role
         */

        roleRepository.delete(role);

    }

}