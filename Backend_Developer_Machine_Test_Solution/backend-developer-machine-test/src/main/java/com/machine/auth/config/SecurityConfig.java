package com.machine.auth.config;

import com.machine.auth.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http,JwtAuthFilter jwtFilter) throws Exception {
        return http.csrf(csrf->csrf.disable())
            .sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth->auth
                .requestMatchers("/api/auth/**","/api/error","/swagger-ui/**","/swagger-ui.html","/v3/api-docs/**","/","/index.html","/favicon.ico").permitAll()
                .requestMatchers("/api/settings/**").hasRole("ADMINISTRATOR")
                .anyRequest().authenticated())
            .addFilterBefore(jwtFilter,UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex->ex.authenticationEntryPoint((req,res,e)->{
                res.setStatus(401);res.setContentType("application/json");res.getWriter().write("{\"message\":\"Unauthorized\"}");
            }).accessDeniedHandler((req,res,e)->{
                res.setStatus(403);res.setContentType("application/json");res.getWriter().write("{\"message\":\"Forbidden\"}");
            })).build();
    }
}
