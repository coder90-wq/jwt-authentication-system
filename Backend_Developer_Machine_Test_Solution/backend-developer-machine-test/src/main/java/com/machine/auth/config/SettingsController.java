package com.machine.auth.config;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.Map;
@RestController
@RequestMapping("/api/settings")
@SecurityRequirement(name = "bearerAuth")
public class SettingsController {
    @GetMapping public Map<String,String> settings(){return Map.of("message","Administrator settings access granted");}
}
