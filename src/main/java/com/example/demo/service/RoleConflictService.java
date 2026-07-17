package com.example.demo.service;

import com.example.demo.dto.RoleConflictRequest;
import com.example.demo.entity.Role;
import com.example.demo.entity.RoleConflict;
import com.example.demo.entity.RoleGroup;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleConflictRepository;
import com.example.demo.repository.RoleGroupRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoleConflictService {

    @Autowired
    private RoleConflictRepository roleConflictRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleGroupRepository roleGroupRepository;

    public List<RoleConflict> getAll() {
        return roleConflictRepository.findAll();
    }

    @Transactional
    public RoleConflict create(RoleConflictRequest request) {

        if (request.getRoleOneId() == null || request.getRoleTwoId() == null) {
            throw new RuntimeException("Both roles are required");
        }
        if (request.getRoleOneId().equals(request.getRoleTwoId())) {
            throw new RuntimeException("A role cannot conflict with itself");
        }

        Long leftId = Math.min(request.getRoleOneId(), request.getRoleTwoId());
        Long rightId = Math.max(request.getRoleOneId(), request.getRoleTwoId());
        Role roleOne = roleRepository.findById(leftId).orElseThrow(() -> new RuntimeException("Role not found"));
        Role roleTwo = roleRepository.findById(rightId).orElseThrow(() -> new RuntimeException("Role not found"));

        if (isAncestorOf(roleOne, roleTwo) || isAncestorOf(roleTwo, roleOne)) {
            throw new RuntimeException("Roles in the same hierarchy cannot be marked as conflicting");
        }

        if (roleConflictRepository.existsByRoleOneIdAndRoleTwoId(leftId, rightId)) {
            throw new RuntimeException("This conflict already exists");
        }

        ensureNoExistingViolations(roleOne, roleTwo);
        RoleConflict conflict = new RoleConflict();
        conflict.setRoleOne(roleOne);
        conflict.setRoleTwo(roleTwo);

        return roleConflictRepository.save(conflict);
    }

    public void delete(Long id) {
        roleConflictRepository.deleteById(id);
    }

    @Transactional
    public void validateRoleForUser(User user, Role candidateRole) {
        validateAgainstRoles(candidateRole, getEffectiveRoles(user), "this user");
    }

    @Transactional
    public void validateRoleForGroup(RoleGroup group, Role candidateRole) {
        ensureNoInternalConflicts(group.getRoles());
        validateAgainstRoles(candidateRole, group.getRoles(), "this role group");
    }

    @Transactional
    public void validateGroupForUser(User user, RoleGroup group) {
        ensureNoInternalConflicts(group.getRoles());
        validateAgainstRolesCollection(group.getRoles(), getEffectiveRoles(user), "this user");
    }

    public boolean rolesConflict(Role first, Role second) {
        if (first == null || second == null || first.getId() == null || second.getId() == null) {
            return false;
        }

        for (Role expandedFirst : expandWithAncestors(first)) {
            for (Role expandedSecond : expandWithAncestors(second)) {
                if (isDirectConflict(expandedFirst.getId(), expandedSecond.getId())) {
                    return true;
                }
            }
        }

        return false;
    }

    private void validateAgainstRoles(Role candidate, Collection<Role> existingRoles, String targetLabel) {
        for (Role existing : existingRoles) {
            if (candidate.getId() != null && existing.getId() != null && candidate.getId().equals(existing.getId())) {
                continue;
            }

            if (rolesConflict(candidate, existing)) {
                throw new RuntimeException(
                        "Role " + candidate.getName() + " conflicts with " + existing.getName() +
                                " and cannot be assigned to " + targetLabel
                );
            }
        }
    }

    private void validateAgainstRolesCollection(Collection<Role> candidateRoles, Collection<Role> existingRoles, String targetLabel) {
        for (Role candidate : candidateRoles) {
            validateAgainstRoles(candidate, existingRoles, targetLabel);
        }
    }

    private void ensureNoInternalConflicts(Collection<Role> roles) {
        List<Role> list = new ArrayList<>(roles);

        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (rolesConflict(list.get(i), list.get(j))) {
                    throw new RuntimeException(
                            "This role group contains conflicting roles: " +
                                    list.get(i).getName() + " and " + list.get(j).getName()
                    );
                }
            }
        }
    }

    private Set<Role> getEffectiveRoles(User user) {
        Map<Long, Role> effective = new LinkedHashMap<>();

        for (Role role : user.getRoles()) {
            addRoleAndAncestors(role, effective);
        }

        for (RoleGroup group : user.getRoleGroups()) {
            for (Role role : group.getRoles()) {
                addRoleAndAncestors(role, effective);
            }
        }

        return new LinkedHashSet<>(effective.values());
    }

    private void addRoleAndAncestors(Role role, Map<Long, Role> map) {
        Role current = role;

        while (current != null && current.getId() != null && !map.containsKey(current.getId())) {
            map.put(current.getId(), current);
            current = current.getParentRole();
        }
    }

    private Set<Role> expandWithAncestors(Role role) {
        Map<Long, Role> expanded = new LinkedHashMap<>();
        addRoleAndAncestors(role, expanded);
        return new LinkedHashSet<>(expanded.values());
    }

    private boolean isDirectConflict(Long firstId, Long secondId) {
        Long left = Math.min(firstId, secondId);
        Long right = Math.max(firstId, secondId);
        return roleConflictRepository.existsByRoleOneIdAndRoleTwoId(left, right);
    }

    private boolean isAncestorOf(Role ancestor, Role descendant) {
        Role current = descendant.getParentRole();

        while (current != null) {
            if (current.getId() != null && current.getId().equals(ancestor.getId())) {
                return true;
            }
            current = current.getParentRole();
        }

        return false;
    }

    private void ensureNoExistingViolations(Role roleOne, Role roleTwo) {
        for (User user : userRepository.findAll()) {
            Set<Role> effective = getEffectiveRoles(user);

            boolean hasOne = containsTargetSide(effective, roleOne);
            boolean hasTwo = containsTargetSide(effective, roleTwo);

            if (hasOne && hasTwo) {
                throw new RuntimeException(
                        "Cannot create this conflict because user " + user.getEmail() +
                                " already has both roles through direct or inherited assignments"
                );
            }
        }

        for (RoleGroup group : roleGroupRepository.findAll()) {
            boolean hasOne = containsTargetSide(group.getRoles(), roleOne);
            boolean hasTwo = containsTargetSide(group.getRoles(), roleTwo);

            if (hasOne && hasTwo) {
                throw new RuntimeException(
                        "Cannot create this conflict because role group " + group.getName() +
                                " already contains both roles"
                );
            }
        }
    }

    private boolean containsTargetSide(Collection<Role> roles, Role target) {
        for (Role role : roles) {
            if (matchesTargetSide(role, target)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesTargetSide(Role role, Role target) {
        for (Role expanded : expandWithAncestors(role)) {
            if (expanded.getId() != null && expanded.getId().equals(target.getId())) {
                return true;
            }
        }
        return false;
    }
}