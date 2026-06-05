package com.example.demo.config;

import com.example.demo.service.CustomUserDetailsService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// like a middleware
@Component
public class JwtFilter extends OncePerRequestFilter { // This filter runs on EVERY incoming request.

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal( //automatically called
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            try {

                String token = authHeader.substring(7); // starts after bearer

                String username = jwtUtil.extractUsername(token); // our jwt is made using email so that is extracted

                UserDetails userDetails =
                        customUserDetailsService.loadUserByUsername(username);// "Go to database, find the user, and return Spring Security authentication object."

                UsernamePasswordAuthenticationToken authentication = // default spring security  auth class. needed to match username from username taken out by token
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,//password Not needed because JWT already authenticated user.
                                userDetails.getAuthorities() // get roles from here to get RBAC
                        );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication); // Stores logged-in user in Spring Security context.

            } catch (JwtException e) {

                System.out.println("Invalid JWT");
            }
        }

        filterChain.doFilter(request, response);
    }
}