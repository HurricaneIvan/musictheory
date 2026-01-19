package com.example.musictheory.utils;

import com.example.musictheory.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
//            String jwt = parseJwt(request);
//            logger.info("AuthTokenFilter cookie ::" + jwt);
            String authHeader = request.getHeader("Authorization");


            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtUtil.extractUsernameFromToken(token);
                String role = jwtUtil.extractUserRoleFromToken(token);
                logger.info("AuthTokenFilter :: role :: " + role);
//                logger.info(SecurityContextHolder.getContext().getAuthentication().toString());

                if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                    UserDetails userDetails = new User(username, "", Collections.singleton(new SimpleGrantedAuthority(role)));
                    logger.info(userDetails.toString());
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null,
                                    userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                }
//                UserDetails userDetails = userService.loadUserByUsername(username);
//                logger.info("AuthTokenFilter :: role :: " + userDetails.toString());

//                UsernamePasswordAuthenticationToken authToken =
//                        new UsernamePasswordAuthenticationToken(userDetails,
//                                null,
//                                userDetails.getAuthorities());

//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        return jwtUtil.getJwtFromCookies(request);
    }

}
