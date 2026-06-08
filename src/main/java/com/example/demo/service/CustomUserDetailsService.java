package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;// Used to create Spring Security roles/authorities.
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;//Converts stream into a list.

@Service
public class CustomUserDetailsService implements UserDetailsService {// This class provides user authentication details automatically

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) // User is db object. UserDetails is spring security object of an interface.
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

                return new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),

                        user.getRoles()
                                .stream()
                                .flatMap(role -> { // For each role, we create:Role authority,Permission authorities,then combine them.

                                    var roleAuthorities =
                                            java.util.stream.Stream.of(
                                                    new SimpleGrantedAuthority( // creates an authority object representing:
                                                            "ROLE_" + role.getName()
                                                    )
                                            );

                                    var permissionAuthorities =
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