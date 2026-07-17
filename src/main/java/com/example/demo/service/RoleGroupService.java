package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.entity.RoleGroup;
import com.example.demo.repository.RoleGroupRepository;
import com.example.demo.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleGroupService {

    @Autowired
    private RoleGroupRepository roleGroupRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleConflictService roleConflictService;

    public RoleGroup create(RoleGroup group) {
        return roleGroupRepository.save(group);
    }

    public List<RoleGroup> getAll() {
        return roleGroupRepository.findAll();
    }

    public RoleGroup getById(Long id) {
        return roleGroupRepository.findById(id).orElseThrow();
    }

    public RoleGroup update(Long id, RoleGroup updated) {
        RoleGroup group = roleGroupRepository.findById(id).orElseThrow();
        group.setName(updated.getName());
        return roleGroupRepository.save(group);
    }

    public void delete(Long id) {
        roleGroupRepository.deleteById(id);
    }

    @Transactional
    public RoleGroup assignRole(Long groupId, Long roleId) {
        RoleGroup group = roleGroupRepository.findById(groupId).orElseThrow();
        Role role = roleRepository.findById(roleId).orElseThrow();

        roleConflictService.validateRoleForGroup(group, role);

        group.getRoles().add(role);
        return roleGroupRepository.save(group);
    }
}