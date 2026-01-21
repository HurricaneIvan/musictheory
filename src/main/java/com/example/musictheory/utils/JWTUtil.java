package com.example.musictheory.utils;

import com.example.musictheory.models.User;
import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;


import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JWTUtil {

    private static final Logger logger = LoggerFactory.getLogger(JWTUtil.class);

    @Value("${jwt.cookie}")
    private String jwtCookie;

    private final SecretKey key = Jwts.SIG.HS256.key().build();

    private static final long EXPIRATION_TIME = 3600000; // 1 hour

    public String generateToken(User user) {
        return Jwts.builder()
                .claim("role", user.getRole())
                .subject(user.getUsername())
                .issuedAt((new Date(System.currentTimeMillis())))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public Claims extractClaims(String token){
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public String extractUsernameFromToken(String token) {
        return extractClaims(token)
                .getSubject();
    }

    public String extractUserRoleFromToken(String token) {
        return extractClaims(token)
                .get("role", String.class);
    }

    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookie, null).path("/api").build();
    }

    public ResponseCookie generateJwtCookie(User user) {
        String jwt = generateToken(user);
        return ResponseCookie.from(jwtCookie, jwt).path("/").maxAge(24 * 60 * 60).httpOnly(true).build();
    }

    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        logger.info("get cookie" + cookie);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }
}
