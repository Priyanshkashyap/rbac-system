package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;// Used to create Spring Security roles/authorities.
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;//Converts stream into a list.

@Service
@Transactional // doesnt let the db session close even after use in a repository method.better than just setting eager loading in the roles table as Hibernate also loads all permissions automatically, even if you never use them.
public class CustomUserDetailsService implements UserDetailsService {// This class provides user authentication details automatically

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) // User is db object. UserDetails is spring security object of an interface.
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

                return new org.springframework.security.core.userdetails.User( // as spring security cannot
                        user.getEmail(),
                        user.getPassword(),

                        user.getRoles()
                                .stream()
                                .flatMap(role -> { // For each role, we create:Role authority,Permission authorities,then combine them.

                                    var roleAuthorities =
                                            java.util.stream.Stream.of(
                                                    new SimpleGrantedAuthority( // creates an authority object representing:
                                                            "ROLE_" + role.getName() // sus
                                                    )
                                            );

                                    var permissionAuthorities = // not intially loaded by lazy loading in the many to many mapping so it looks for database session but it ends after repository execution.
                                            role.getPermissions()
                                                    .stream()
                                                    .map(permission ->
                                                            new SimpleGrantedAuthority( //creates an authority object representing:
                                                                    permission.getName()
                                                            )
                                                    );

                                    return java.util.stream.Stream.concat(
                                            roleAuthorities,
                                            permissionAuthorities
                                    );
                                })
                                .collect(Collectors.toList()) // receives as list
                );// flatmap converts everything to
                // [
                // ROLE_ADMIN,
                // USER_READ,
                // ROLE_MANAGER,
                // USER_CREATE
                //]

                //without flatmap: [
                //  [ROLE_ADMIN, USER_READ],
                //  [ROLE_MANAGER, USER_CREATE]
                //]
    }
}