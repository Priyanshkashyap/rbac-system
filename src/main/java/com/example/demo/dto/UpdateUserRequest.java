package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private boolean active;

}