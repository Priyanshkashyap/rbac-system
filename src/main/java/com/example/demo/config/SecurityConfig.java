package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Incoming Request
//   ↓
//Check FilterChain 1 (match?)
//   ↓
//Check FilterChain 2 (match?)
//   ↓
//Apply FIRST matching chain or keep manual order
@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean // The RETURN VALUE of this method becomes a Spring bean
    // as configurations automatically apply the beans everywhere we dont need to inject this bean elsewhere.
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users").permitAll()
                        .requestMatchers("/api/users/login").permitAll()
                        .requestMatchers("/api/users/forgot-password").permitAll()
                        .requestMatchers("/api/users/reset-password").permitAll()
                        .requestMatchers("/api/users/").permitAll()
                        .requestMatchers("/api/users/*/complete-profile").permitAll()
                        .requestMatchers("/api/users/**")
                        .hasRole("ADMIN")// every other api of users except users and users/login are protected
                        .requestMatchers("/api/manage/**").hasAuthority("USER_DELETE")// only ppl with this authority can access these urls
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);//“Run my JWT filter BEFORE Spring’s normal login authentication filter.”

        return http.build();
    }
}

/*Application starts
↓
Spring scans packages like component,service,controller,etc.
↓
Finds SecurityConfig
↓
Creates SecurityConfig bean
↓
Looks for @Bean methods
↓
Executes securityFilterChain()
↓
Stores returned object as bean
↓
Security system initialized
↓
App starts listening for requests
 */