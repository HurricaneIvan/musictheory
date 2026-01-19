package com.example.musictheory.utils;

import com.example.musictheory.models.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;


import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private static final Logger logger = LoggerFactory.getLogger(JWTUtil.class);

    @Value("${jwt.cookie}")
    private String jwtCookie;

    private final String SECRET_KEY = "cookiemonstersesamestreet";
//    private static final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);


    private static final long EXPIRATION_TIME = 3600000; // 1 hour

    private SecretKey secretKey;

    public JWTUtil() {
        this.secretKey = Jwts.SIG.HS256.key().build();
    }
    private SecretKey key() {
        return secretKey;
    }

//    private SecretKey key() {
//        return Jwts.SIG.HS256.key().build();
//    }

    public String generateToken(User user) {
        logger.info("generateToken :: user {}", user.toString());
        return Jwts.builder()
                .claim("role", user.getRole()) // .toString is null why?
                .subject(user.getUsername())
                .issuedAt((new Date(System.currentTimeMillis())))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key())
                .compact();
    }


    public String extractUsernameFromToken(String token) {
        return Jwts.parser()
//                .verifyWith(key())
                .setSigningKey(key())
                .build()
                .parseSignedClaims(token)
                .getBody()
                .getSubject();
    }

    public String extractUserRoleFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(key())
                .build()
                .parseSignedClaims(token)
                .getBody()
                .get("role", String.class);
    }
//
//    /**
//     * Checks username match and token expiry.
//     * @param token
//     * @param userDetails
//     * @return
//     */
//    public boolean validateToken(String token, UserDetails userDetails) {
//        final String username = extractUsernameFromToken(token);
//        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
//    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().verifyWith(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
        return expiration.before(new Date());
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
