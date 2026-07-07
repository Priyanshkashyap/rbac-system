package com.example.demo.service;
import com.example.demo.entity.AuthProvider;
import com.example.demo.config.JwtUtil;
import com.example.demo.dto.CompleteProfileRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.ResetPasswordRequest;
import com.example.demo.dto.UpdateUserRequest;
import com.example.demo.entity.AuthProvider;
import com.example.demo.entity.Role;
import com.example.demo.entity.RoleGroup;
import com.example.demo.repository.RoleGroupRepository;
import com.example.demo.repository.RoleRepository;

import java.util.List;
import java.util.Optional;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.util.PasswordValidator;

@Service
public class UserService {

    @Autowired
    private SessionService sessionService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleGroupRepository roleGroupRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CaptchaVerificationService captchaVerificationService;
    public User createUser(User user) {

        if (!PasswordValidator.isStrong(user.getPassword())) {
            throw new RuntimeException(
                    "Password must contain uppercase, lowercase, number and special character"
            );
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setProvider(AuthProvider.MANUAL);
        Role defaultRole = roleRepository.findByName("USER").orElseThrow(() -> new RuntimeException("Default role missing"));
        user.getRoles().add(defaultRole);
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
    public LoginResponse login(String email, String password,String captchaToken) {
        if(!captchaVerificationService.verifyCaptcha(captchaToken))
        {
            throw new RuntimeException("Captcha validation failed");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if(user.getProvider() != AuthProvider.MANUAL){

            throw new RuntimeException("Use "+ user.getProvider()+ " Sign In");
        }
        if (!user.isActive()) {
            throw new RuntimeException(
                    "User is deactivated"
            );
        }

        if (!passwordEncoder.matches(password, user.getPassword())) { // password encoder is already an interface whose bean we created in security config file . it acts as bcrypt
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        sessionService.createSession(user, token, request);

        return new LoginResponse( // instead of token we are also returning first login as a form of another dto
                token,
                user.isFirstLogin(),
                user.getId()
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
        if (!PasswordValidator.isStrong(
                request.getNewPassword()
        )) {

            throw new RuntimeException(
                    "Weak password"
            );
        }
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
        if (!PasswordValidator.isStrong(
                request.getNewPassword()
        )) {

            throw new RuntimeException(
                    "Weak password"
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
    public User assignGroup(
            Long userId,
            Long groupId
    ) {

        User user =
                userRepository.findById(userId)
                        .orElseThrow();

        RoleGroup group =
                roleGroupRepository.findById(groupId)
                        .orElseThrow();

        user.getRoleGroups().add(group);

        return userRepository.save(user);
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow();
    }
    public User updateUser(
            Long id,
            UpdateUserRequest request
    ) {

        User user =
                userRepository.findById(id)
                        .orElseThrow();

        user.setFirstName(
                request.getFirstName()
        );

        user.setLastName(
                request.getLastName()
        );

        user.setPhoneNumber(
                request.getPhoneNumber()
        );

        user.setActive(
                request.isActive()
        );

        return userRepository.save(user);
    }
    public User deactivateUser(Long id) {

        User user =
                userRepository.findById(id)
                        .orElseThrow();

        user.setActive(false);

        return userRepository.save(user);
    }
    public User updateTheme(
            Long userId,
            String theme
    ) {

        User user =
                userRepository.findById(userId)
                        .orElseThrow();

        if (!theme.equals("LIGHT")
                && !theme.equals("DARK")
                && !theme.equals("ADMIN")) {

            throw new RuntimeException(
                    "Invalid theme"
            );
        }

        user.setProfileTheme(theme);

        return userRepository.save(user);
    }
    public void permanentDeleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        userRepository.delete(user);
    }
}

// Controller → Service → Repository → DB