package com.cloudstorage.Cloudo.config;

import com.cloudstorage.Cloudo.models.User;
import com.cloudstorage.Cloudo.Repo.UserRepo;
import com.cloudstorage.Cloudo.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepo userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Check JWT existence
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);

        // If username exists and no auth context yet
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Fetch user from DB
            User user = userRepository.findByUsername(username).orElse(null);

            if (user != null && jwtService.isTokenValid(jwt, user)) {
                // Get role from JWT claims or DB
                String role = jwtService.extractRole(jwt);
                if (role == null) {
                    role = user.getRole(); // fallback from DB
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user, // principal is your User entity
                        null,
                        List.of(new SimpleGrantedAuthority(role))
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
