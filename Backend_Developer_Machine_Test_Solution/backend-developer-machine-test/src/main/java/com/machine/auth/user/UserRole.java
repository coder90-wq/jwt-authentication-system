package com.machine.auth.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Locale;

public enum UserRole {
    STANDARD_USER, ADMINISTRATOR;

    // Accept both API enum values and the exact labels used in the assignment PDF.
    @JsonCreator
    public static UserRole from(String value) {
        if (value == null) return null;
        return switch (value.trim().toLowerCase(Locale.ROOT).replace('_', ' ')) {
            case "standard user" -> STANDARD_USER;
            case "administrator" -> ADMINISTRATOR;
            default -> throw new IllegalArgumentException("User type must be Standard User or Administrator");
        };
    }
}
