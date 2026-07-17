package com.example.demo.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleConflictRequest {
    private Long roleOneId;
    private Long roleTwoId;
}