package com.machine.auth.user;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {
    @Test
    void storesUsersInMemoryWithOnlyPasswordHash() {
        UserRepository users = new UserRepository();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "Pass@123";
        String passwordHash = encoder.encode(rawPassword);

        users.save(new AppUser("demo_user", "demo_user@example.com", passwordHash, UserRole.STANDARD_USER));

        AppUser stored = users.findByEmail("DEMO_USER@example.com").orElseThrow();
        assertNotEquals(rawPassword, stored.getPasswordHash());
        assertTrue(stored.getPasswordHash().startsWith("$2a$"));
        assertTrue(encoder.matches(rawPassword, stored.getPasswordHash()));
        assertTrue(users.existsByUsername("DEMO_USER"));
    }
}
