package com.example.demo.config;

import com.example.demo.service.JWTService;
import com.example.demo.service.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final ApplicationContext applicationContext;
    private final JWTService jwtService;

    public JwtFilter(ApplicationContext applicationContext, JWTService jwtService) {
        this.applicationContext = applicationContext;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String userEmail = null;
        String jwtToken = null;

        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
            userEmail = jwtService.extractUserName(jwtToken);
        }

        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = applicationContext.getBean(MyUserDetailsService.class).loadUserByUsername(userEmail);

            if(jwtService.validateToken(jwtToken, userDetails)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
