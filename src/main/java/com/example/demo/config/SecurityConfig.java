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
                        .requestMatchers("/api/users/login").permitAll()
                        .requestMatchers("/api/users").permitAll()
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