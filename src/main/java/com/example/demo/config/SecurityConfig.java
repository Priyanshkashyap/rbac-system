package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public endpoints
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers("/api/users/login").permitAll()
                        .requestMatchers("/api/users/forgot-password").permitAll()
                        .requestMatchers("/api/users/reset-password").permitAll()
                        .requestMatchers("/api/users/*/complete-profile").permitAll()

                        // User APIs
                        .requestMatchers(HttpMethod.GET, "/api/users").hasAuthority("USER_READ")
                        .requestMatchers(HttpMethod.GET, "/api/users/*").hasAuthority("USER_READ")
                        .requestMatchers(HttpMethod.PUT, "/api/users/*").hasAuthority("USER_UPDATE")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/*").hasAuthority("USER_DELETE")

                        // User Role Assignment
                        .requestMatchers("/api/users/*/roles/*").hasAuthority("ROLE_ASSIGN")

                        // User Group Assignment
                        .requestMatchers("/api/users/*/groups/*").hasAuthority("GROUP_ASSIGN")

                        // Theme
                        .requestMatchers("/api/users/*/theme").authenticated()

                        // Export
                        .requestMatchers("/api/users/export").hasAuthority("EXPORT_USER")

                        // Audit
                        .requestMatchers("/api/audit/**").hasAuthority("AUDIT_READ")

                        // Permissions
                        .requestMatchers("/api/permissions/**").hasAuthority("PERMISSION_MANAGE")

                        // Roles
                        .requestMatchers("/api/roles/**").hasAuthority("ROLE_MANAGE")
                        .requestMatchers("/api/roles/*/permissions/*").hasAuthority("PERMISSION_ASSIGN")

                        // Role Conflicts / Separation of Duties
                        .requestMatchers("/api/role-conflicts/**").hasAuthority("ROLE_MANAGE")

                        // Role Groups
                        .requestMatchers("/api/role-groups/**").hasAuthority("ROLE_GROUP_MANAGE")

                        // Special
                        .requestMatchers("/api/manage/**").hasAuthority("USER_DELETE")
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        .requestMatchers("/instance").permitAll()

                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth.successHandler(oAuth2SuccessHandler))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}