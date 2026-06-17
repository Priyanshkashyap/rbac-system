package com.example.demo.util;

public class PasswordValidator {
    public static boolean isStrong(String password) {

        return password.matches( // for regex
                "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$"//At least one lowercase letter,uppercase,digit, min length 8
        );
    }
}
