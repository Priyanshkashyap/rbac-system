package com.example.demo.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String jwtToken;
    private LocalDateTime loginTime;
    private LocalDateTime lastActivity;
    private String ipAddress;
    private String browser;
    private boolean active = true;
    // UserSession.java
    @JsonBackReference // Jackson never tries to serialize user.
    @ManyToOne(fetch = FetchType.LAZY) // just keeping lazy loading wont work Spring Boot says:I need to send this List<User> as JSON."So it calls Jackson.Jackson doesn't know anything about Hibernate or lazy loading.
    // just lazy helps while selecting stuff when big things not required to save time not while exporting data as json
    private User user;


}