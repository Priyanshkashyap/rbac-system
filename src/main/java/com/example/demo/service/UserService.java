package com.example.demo.service;
import com.example.demo.entity.Role;
import com.example.demo.repository.RoleRepository;
import java.util.Optional;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    public User createUser(User user) {
        return userRepository.save(user); 
    }
    public User assignRole(Long userId, String roleName) {

        User user = userRepository.findById(userId) // this optional function returns Optional[User object] or Optional.empty(). if the 2nd case is true is leads to  the orelsethrow function
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleRepository.findByName(roleName) // same here
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.getRoles().add(role);

        return userRepository.save(user);
    }
}

// Controller → Service → Repository → DB