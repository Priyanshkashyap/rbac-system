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

        return new org.springframework.security.core.userdetails.User( //This is Spring Security's internal User object.
                user.getEmail(),
                user.getPassword(), // Spring Security compares this hashed password with login password.
                user.getRoles()// all roles
                        .stream() // process it one by one
                        .map(role ->
                                new SimpleGrantedAuthority("ROLE_" + role.getName())) // takes out name of role from something like ROLE_USER given already
                        .collect(Collectors.toList())// Converts stream into list of authorities.
        );
    }
}