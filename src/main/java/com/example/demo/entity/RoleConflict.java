package com.example.demo.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "role_conflicts", uniqueConstraints = @UniqueConstraint(columnNames = {"role_one_id", "role_two_id"})) // It means the combination of these two columns must be unique.
public class RoleConflict {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_one_id", nullable = false)
    @JsonIgnoreProperties({"parentRole", "childRoles"}) // parent role if we fetch it gets child nodes which gets parent and so on so to stop it...
    private Role roleOne;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_two_id", nullable = false)
    @JsonIgnoreProperties({"parentRole", "childRoles"})
    private Role roleTwo;
}