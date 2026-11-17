package com.machine.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.URI;

@Controller
public class FrontendRedirectController {
    private final URI frontendUrl;

    public FrontendRedirectController(@Value("${app.frontend.url:http://localhost:3000}") URI frontendUrl) {
        this.frontendUrl = frontendUrl;
    }

    @GetMapping({"/", "/index.html"})
    public ResponseEntity<Void> frontend() {
        return ResponseEntity.status(HttpStatus.FOUND).location(frontendUrl).build();
    }
}
