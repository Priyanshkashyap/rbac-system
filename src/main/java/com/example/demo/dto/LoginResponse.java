package com.example.demo.dto;

public class LoginResponse {

    private String token;
    private boolean firstLogin;

    public LoginResponse(String token, boolean firstLogin) {
        this.token = token;
        this.firstLogin = firstLogin;
    }

    public String getToken() {
        return token;
    }

    public boolean isFirstLogin() {
        return firstLogin;
    }
}