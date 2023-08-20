package com.staroot.im.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    //FAIL1
    //private final String secret = "your-secret-key"; // Replace with your own secret key
    //FAIL2
    //private final String secret = "eW91ci1zZWNyZXQta2V5";

    //SUC1
    //private final String secret = "VvNDl6XkZOm8d5KjH6bjUwIvNg3LrItG0CxhQnwoIn3sN1Pk4/JvQfwzIiEgreduPlbVqEcX/AG5EhD8KvNSOw==";
    //SUC2
    private final String secret = "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567";
    private final long expirationMs = 3600000; // 1 hour

    @Value("${jwt.secret}")
    private String secretForRefresh;

    @Value("${jwt.refreshExpiration}")
    private long refreshExpiration;

    public String generateJwtToken(String username) {
        Date expiration = new Date(System.currentTimeMillis() + expirationMs);
        String randomSecret = generateSafeToken(64);
        logger.debug("randomSecret::"+randomSecret);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                //.signWith(SignatureAlgorithm.HS512, randomSecret)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
    private String createRefreshToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createRefreshToken(claims, username, refreshExpiration);
    }
    private String generateSafeToken(int size) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[size];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // Handle invalid token
        }
        return false;
    }
}

