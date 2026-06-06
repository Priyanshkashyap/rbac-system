package com.example.demo.entity;

import jakarta.persistence.*;
// Permission = actual action. eg. USER_CREATE,USER_DELETE,etc.
@Entity
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Permission() {}

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}