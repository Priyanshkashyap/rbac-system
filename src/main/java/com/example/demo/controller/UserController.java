package com.example.demo.controller;

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
    @PutMapping("/{userId}/roles/{roleName}")
    public User assignRole(
            @PathVariable Long userId, // stores info from path name
            @PathVariable String roleName
    ) {
        return userService.assignRole(userId, roleName);
    }
}