package com.example.demo.service;
import com.example.demo.entity.User;
import com.example.demo.entity.UserSession;
import com.example.demo.repository.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
public class SessionService {

    @Autowired
    private UserSessionRepository sessionRepository;

    public void createSession(User user, String jwt, HttpServletRequest request)
    {

        UserSession session = new UserSession();
        session.setUser(user);
        session.setJwtToken(jwt);
        session.setLoginTime(LocalDateTime.now());
        session.setLastActivity(LocalDateTime.now());
        session.setActive(true);
        session.setIpAddress(request.getRemoteAddr());
        session.setBrowser(request.getHeader("User-Agent"));
        sessionRepository.save(session);
    }

}