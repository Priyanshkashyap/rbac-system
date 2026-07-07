package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.User;
import com.example.demo.service.ExcelExportService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
//   @Autowired tells to Find an object (bean) of this type and automatically give it to me.
    @Autowired
    private UserService userService;
    @Autowired
    private ExcelExportService excelExportService;

    @PostMapping
    public User createUser(@RequestBody User user) { // @RequestBody Take JSON data from the HTTP request body and convert it into a Java object.
        return userService.createUser(user);
    }
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) { // takes input from request body
        return userService.login(
                request.getEmail(),
                request.getPassword(),
                request.getCaptchaToken()
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
    @PutMapping("/{userId}/groups/{groupId}")
    public User assignGroup(
            @PathVariable Long userId,
            @PathVariable Long groupId
    ) {
        return userService.assignGroup(
                userId,
                groupId
        );
    }
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    @GetMapping("/{id}")
    public User getUser(
            @PathVariable Long id
    ) {
        return userService.getUser(id);
    }
    @PutMapping("/{id}")
    public User updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request
    ) {
        return userService.updateUser(
                id,
                request
        );
    }
    @PutMapping("/{id}/theme")
    public User updateTheme(
            @PathVariable Long id,
            @RequestBody ThemeRequest request
    ) {

        return userService.updateTheme(
                id,
                request.getTheme()
        );
    }
    @DeleteMapping("/{id}")
    public User deactivateUser(
            @PathVariable Long id
    ) {
        return userService.deactivateUser(id);
    }
    @GetMapping("/export")
    // apache poi dependency
    public ResponseEntity<byte[]> exportUsers() {//Normally you might return: some data type But ResponseEntity lets you control:Response body,HTTP status code,Headers
// The body contains raw bytes.
        byte[] excelFile = excelExportService.exportUsers();// Generate Excel File.These are the actual binary contents of users.xlsx.

        HttpHeaders headers = new HttpHeaders();//Creates an empty container for HTTP headers.

        headers.setContentType( // The browser uses this header to understand what type of file is being returned.
                MediaType.parseMediaType(// Converts a String into a Spring MediaType object.The response contains an Excel (.xlsx) file.
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"//This MIME type is the official content type for Excel files.
                )
        );

        headers.setContentDisposition(// "Don't display this response in the browser. Download it as a file."Content-Disposition: attachment
                ContentDisposition
                        .attachment()
                        .filename("users.xlsx")
                        .build()
        );

        return ResponseEntity.ok() //Creates a response with HTTP status:200 OK
                .headers(headers)
                .body(excelFile);
    }
    @DeleteMapping("/{id}/permanent")
    public String permanentDeleteUser(
            @PathVariable Long id
    ) {

        userService.permanentDeleteUser(id);

        return "User permanently deleted";
    }
}