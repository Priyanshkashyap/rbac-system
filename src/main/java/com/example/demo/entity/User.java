package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
@Getter
@Setter
@Entity
@Table(name = "users") // because user is reserved in PostgreSQL sometimes.
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;
    private boolean firstLogin = true;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String secretQuestion;
    private String secretAnswer;
    private boolean active = true;
    private String profileTheme = "LIGHT";

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable( // Because in SQL, a Many-to-Many relation cannot be stored directly.So Hibernate creates a bridge table:
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id") // join only but from other side
    )

    private Set<Role> roles = new HashSet<>(); // set of role objects

    @ManyToMany(fetch = FetchType.EAGER)// lol
    @JoinTable(
            name = "user_role_groups",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<RoleGroup> roleGroups = new HashSet<>();// by this it knows that group and user are joined
}