package com.example.demo.repository;
import java.util.Optional;
import com.example.demo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

// jpa repository stuff gets scanned even without any component annotation
public interface RoleRepository extends JpaRepository<Role, Long> { // using generics it decided what to return as a type in every object and a find by id method as well
    Optional<Role> findByName(String name);
}