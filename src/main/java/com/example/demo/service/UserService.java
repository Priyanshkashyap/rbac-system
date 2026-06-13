package com.example.demo.service;
import com.example.demo.config.JwtUtil;
import com.example.demo.dto.CompleteProfileRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.ResetPasswordRequest;
import com.example.demo.entity.Role;
import com.example.demo.repository.RoleRepository;
import java.util.Optional;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JwtUtil jwtUtil;
    public User createUser(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

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
    public LoginResponse login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) { // password encoder is already an interface whose bean we created in security config file . it acts as bcrypt
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new LoginResponse( // instead of token we are also returning first login as a form of another dto
                token,
                user.isFirstLogin()
        );
    }
    public User completeProfile(
            Long userId,
            CompleteProfileRequest request
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());

        user.setSecretQuestion(
                request.getSecretQuestion()
        );

        user.setSecretAnswer(
                request.getSecretAnswer()
        );

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        user.setFirstLogin(false);

        return userRepository.save(user);
    }
    public String getSecretQuestion(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return user.getSecretQuestion();
    }
    public String resetPassword(
            ResetPasswordRequest request
    ) {

        User user = userRepository.findByEmail(
                request.getEmail()
        ).orElseThrow();

        if (!user.getSecretAnswer()
                .equalsIgnoreCase(request.getAnswer())) {

            throw new RuntimeException(
                    "Wrong secret answer"
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);

        return "Password reset successful";
    }
}

// Controller → Service → Repository → DB