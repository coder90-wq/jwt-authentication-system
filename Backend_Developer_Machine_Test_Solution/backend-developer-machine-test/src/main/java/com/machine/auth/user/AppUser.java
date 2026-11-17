package com.machine.auth.user;

public class AppUser {
    private final String username;
    private final String email;
    private final String passwordHash;
    private final UserRole role;

    public AppUser(String username, String email, String passwordHash, UserRole role) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public UserRole getRole() { return role; }
}
