package com.example.demo.config;
import com.example.demo.entity.Role;
import com.example.demo.entity.AuthProvider;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.SessionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
// This method is called by Spring automatically after Google says:"Yes, I verified this user."before this control first comes to spring through an auto generate url then oauth is identified then control goes to google to verify user then details come in this function while password is kept hidden.
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private SessionService sessionService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException // for normal controllers, Spring internally still has a request and response object because every HTTP request needs them, but your method doesn't need direct access to them.but this implemented interface has it
    { // authentication has all the authenticated user details
        try {
            System.out.println("Server Name : " + request.getServerName());
            System.out.println("Server Port : " + request.getServerPort());
            System.out.println("Request URL : " + request.getRequestURL());
            System.out.println("Request URI : " + request.getRequestURI());
            System.out.println("Host Header : " + request.getHeader("Host"));
            System.out.println("X-Forwarded-Host : " + request.getHeader("X-Forwarded-Host"));
            System.out.println("X-Forwarded-Port : " + request.getHeader("X-Forwarded-Port"));
            System.out.println("X-Forwarded-Proto : " + request.getHeader("X-Forwarded-Proto"));


            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication; // OAuth2AuthenticationToken is subclass of Authentication so parent reference variable can be type caseted into child's reference variable
            String provider = token.getAuthorizedClientRegistrationId();
            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal(); // all  details converted to oauth2user object

            String email = oauthUser.getAttribute("email");
            String firstName = oauthUser.getAttribute("given_name");
            String lastName = oauthUser.getAttribute("family_name");
            Optional<User> existingUser = userRepository.findByEmail(email);
            User user;
            if (existingUser.isPresent()) {
                user = existingUser.get();
            } else {
                user = new User();
                user.setEmail(email);
                user.setUsername(email);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setActive(true);
                user.setFirstLogin(true);
                user.setProfileTheme("LIGHT");

                if (provider.equals("google")) {
                    user.setProvider(AuthProvider.GOOGLE);
                } else {
                    user.setProvider(AuthProvider.GITHUB);
                }
                userRepository.save(user);
            }
            String jwt = jwtUtil.generateToken(email);
            sessionService.createSession(user, jwt, request);

            System.out.println("JWT = " + jwt);
            System.out.println("Redirecting to frontend...");

            response.sendRedirect(
                    "http://localhost:5173/oauth-success"
                            + "?token=" + URLEncoder.encode(jwt, StandardCharsets.UTF_8)
                            + "&userId=" + user.getId()
            );//export the JWT into a form that is safe to put inside a URL.
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}