package com.example.demo.service;

import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Map<Long, Role> effectiveRoles = new LinkedHashMap<>();

        user.getRoles().forEach(role -> collectRoleHierarchy(role, effectiveRoles));
        user.getRoleGroups().forEach(group ->
                group.getRoles().forEach(role -> collectRoleHierarchy(role, effectiveRoles))
        );

        Set<SimpleGrantedAuthority> authorities = new LinkedHashSet<>();

        for (Role role : effectiveRoles.values()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

            for (Permission permission : role.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    private void collectRoleHierarchy(Role role, Map<Long, Role> roleMap) {
        Role current = role;

        while (current != null && current.getId() != null && !roleMap.containsKey(current.getId())) {
            roleMap.put(current.getId(), current);
            current = current.getParentRole();
        }
    }
}