package com.machine.auth.security;

import com.machine.auth.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwt; private final TokenBlacklist blacklist; private final UserRepository users;
    public JwtAuthFilter(JwtService jwt,TokenBlacklist blacklist,UserRepository users){this.jwt=jwt;this.blacklist=blacklist;this.users=users;}
    @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)
        throws ServletException,IOException {
        String header=request.getHeader("Authorization");
        if(header!=null && header.startsWith("Bearer ")) {
            String token=header.substring(7);
            try {
                if(!blacklist.isRevoked(token)) {
                    String email=jwt.subject(token);
                    users.findByEmail(email).ifPresent(user -> {
                        var auth=new UsernamePasswordAuthenticationToken(user.getEmail(),null,
                            List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole().name())));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    });
                }
            } catch (RuntimeException ignored) { /* Invalid/expired token remains unauthenticated. */ }
        }
        chain.doFilter(request,response);
    }
}
