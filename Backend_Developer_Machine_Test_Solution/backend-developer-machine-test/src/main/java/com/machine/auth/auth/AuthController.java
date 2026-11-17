package com.machine.auth.auth;

import com.machine.auth.security.*;
import com.machine.auth.user.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final UserRepository users; private final PasswordEncoder encoder;
    private final JwtService jwt; private final TokenBlacklist blacklist; private final OtpService otp;
    private final Set<String> restricted;
    private final Map<String,Boolean> otpVerified=new ConcurrentHashMap<>();

    public AuthController(UserRepository users,PasswordEncoder encoder,JwtService jwt,
        TokenBlacklist blacklist,OtpService otp,@Value("${app.security.restricted-countries}") String restricted) {
        this.users=users;this.encoder=encoder;this.jwt=jwt;this.blacklist=blacklist;this.otp=otp;
        this.restricted=Set.of(restricted.split(","));
    }

    @PostMapping("/auth/otp/request")
    public Map<String,String> requestOtp(@Valid @RequestBody AuthDtos.OtpRequest request){
        String code=otp.create(request.email());
        // Demo only: OTP is returned in response. Send it through an email/SMS provider in production.
        return Map.of("message","OTP generated (demo mode)","demoOtp",code);
    }
    @PostMapping("/auth/otp/verify")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody AuthDtos.VerifyOtpRequest request){
        if(!otp.verify(request.email(),request.otp())) return ResponseEntity.badRequest().body(Map.of("message","Invalid or expired OTP"));
        otpVerified.put(request.email(),true);
        return ResponseEntity.ok(Map.of("message","OTP verified"));
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody AuthDtos.SignupRequest request,HttpServletRequest http){
        String country=request.countryCode().trim().toUpperCase(Locale.ROOT);
        if(restricted.contains(country)) return ResponseEntity.status(403).body(Map.of("message","Sign-up is not available in your country"));
        if(!Boolean.TRUE.equals(otpVerified.remove(request.email()))) return ResponseEntity.badRequest().body(Map.of("message","Verify OTP before signing up"));
        if(users.existsByEmail(request.email())) return ResponseEntity.badRequest().body(Map.of("message","Email already registered"));
        if(users.existsByUsername(request.username())) return ResponseEntity.badRequest().body(Map.of("message","Username already taken"));
        AppUser user=users.save(new AppUser(request.username(),request.email(),encoder.encode(request.password()),request.userType()));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message","Registration successful","username",user.getUsername()));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthDtos.LoginRequest request){
        var user=users.findByEmail(request.email()).orElse(null);
        if(user==null || !encoder.matches(request.password(),user.getPasswordHash()))
            return ResponseEntity.status(401).body(Map.of("message","Login Failed","errorEndpoint","/api/error"));
        return ResponseEntity.ok(new AuthDtos.AuthResponse(jwt.generate(user),"Login successful"));
    }

    @GetMapping("/dashboard")
    @SecurityRequirement(name = "bearerAuth")
    public Map<String,String> dashboard(org.springframework.security.core.Authentication auth){
        var user=users.findByEmail(auth.getName()).orElseThrow();
        return Map.of("message","Welcome "+user.getUsername());
    }

    @GetMapping("/error")
    public ResponseEntity<?> loginError(){return ResponseEntity.status(401).body(Map.of("message","Login Failed"));}

    @PostMapping("/auth/logout")
    @SecurityRequirement(name = "bearerAuth")
    public Map<String,String> logout(HttpServletRequest request,org.springframework.security.core.Authentication auth){
        String token=request.getHeader("Authorization").substring(7);
        blacklist.revoke(token);
        var user=users.findByEmail(auth.getName()).orElseThrow();
        return Map.of("message",user.getUsername()+" Logged Out");
    }
}
