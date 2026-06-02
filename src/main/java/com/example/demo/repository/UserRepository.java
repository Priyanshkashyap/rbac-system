package com.example.demo.repository;
import java.util.Optional;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
//Spring Data JPA reads the method name and converts it into SQL automatically.
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id); // optional means may or may not return a user object
    Optional<User> findByEmail(String email);
}
