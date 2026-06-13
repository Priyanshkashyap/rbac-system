package com.example.demo.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompleteProfileRequest {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String secretQuestion;
    private String secretAnswer;
    private String newPassword;
}