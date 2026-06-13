package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
//   @Autowired tells to Find an object (bean) of this type and automatically give it to me.
    @Autowired
    private UserService userService;

    @PostMapping
    public User createUser(@RequestBody User user) { // @RequestBody Take JSON data from the HTTP request body and convert it into a Java object.
        return userService.createUser(user);
    }
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) { // takes input from request body
        return userService.login(
                request.getEmail(),
                request.getPassword()
        );
    }
    @PutMapping("/{userId}/roles/{roleName}")
    public User assignRole(
            @PathVariable Long userId, // stores info from path name
            @PathVariable String roleName
    ) {
        return userService.assignRole(userId, roleName);
    }
    @PutMapping("/{id}/complete-profile")
    public User completeProfile(
            @PathVariable Long id,
            @RequestBody CompleteProfileRequest request
    ) {
        return userService.completeProfile(id, request);
    }
    @PostMapping("/forgot-password")
    public String forgotPassword(
            @RequestBody ForgotPasswordRequest request
    ) {
        return userService.getSecretQuestion(
                request.getEmail()
        );
    }
    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestBody ResetPasswordRequest request
    ) {
        return userService.resetPassword(request);
    }
}