package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage")
public class ManagementController {

    @GetMapping("/delete-user")
    public String deleteUserAccess() {
        return "You have USER_DELETE permission 🔥";
    }
}