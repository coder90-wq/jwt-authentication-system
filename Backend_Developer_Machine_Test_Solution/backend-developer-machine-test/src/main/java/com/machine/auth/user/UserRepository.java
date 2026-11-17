package com.machine.auth.user;

import org.springframework.stereotype.Repository;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserRepository {
    private final ConcurrentHashMap<String, AppUser> usersByEmail = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> emailByUsername = new ConcurrentHashMap<>();

    public Optional<AppUser> findByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(normalize(email)));
    }

    public boolean existsByEmail(String email) {
        return usersByEmail.containsKey(normalize(email));
    }

    public boolean existsByUsername(String username) {
        return emailByUsername.containsKey(username.toLowerCase(Locale.ROOT));
    }

    public AppUser save(AppUser user) {
        String email = normalize(user.getEmail());
        String username = user.getUsername().toLowerCase(Locale.ROOT);
        if (usersByEmail.containsKey(email) || emailByUsername.containsKey(username)) {
            throw new IllegalArgumentException("Email or username already registered");
        }
        String existingEmail = emailByUsername.putIfAbsent(username, email);
        if (existingEmail != null) throw new IllegalArgumentException("Username already taken");
        AppUser existing = usersByEmail.putIfAbsent(email, user);
        if (existing != null) {
            emailByUsername.remove(username, email);
            throw new IllegalArgumentException("Email already registered");
        }
        return user;
    }

    private String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
