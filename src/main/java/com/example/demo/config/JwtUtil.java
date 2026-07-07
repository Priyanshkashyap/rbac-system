package com.example.demo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${SECRET_KEY}")
    private  String SECRET_KEY ;

    private SecretKey getSigningKey() { // This method converts your normal string secret into a proper cryptographic key object that JWT library can use securely.
        return Keys.hmacShaKeyFor( // these classes are injected by the packages we installed
                SECRET_KEY.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateToken(String username) { // has all 2 components required to make a key i.e. secret key in hmacsha form and the username as input to the function

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {

        Claims claims = Jwts.parser()//Creates JWT parser object.
                .verifyWith(getSigningKey())// Use this secret key to verify token signature(3rd part of token)
                .build()//"Parser setup complete"
                .parseSignedClaims(token)//Actually parses + validates JWT.
                .getPayload();//Extracts JWT payload/body.

        return claims.getSubject();//get username
    }
}