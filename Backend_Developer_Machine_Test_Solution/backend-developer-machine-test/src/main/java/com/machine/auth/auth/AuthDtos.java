package com.machine.auth.auth;

import com.machine.auth.user.UserRole;
import jakarta.validation.constraints.*;

public class AuthDtos {
    // The assignment explicitly requires both @ and underscore in the email.
    private static final String EMAIL_WITH_UNDERSCORE =
        "^[A-Za-z0-9._%+-]*_[A-Za-z0-9._%+-]*@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public record SignupRequest(
        @NotBlank @Size(min=8,max=50) String username,
        @NotBlank @Pattern(regexp=EMAIL_WITH_UNDERSCORE, message="must be a valid email containing _ before @, e.g. demo_user@example.com") String email,
        @NotBlank @Pattern(regexp="^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,12}$",
            message="must be 8-12 chars and include uppercase, lowercase, number and special character") String password,
        @NotNull UserRole userType,
        @Pattern(regexp="(?i)^[A-Z]{2}$", message="must be a two-letter country code") String countryCode
    ) {}
    public record LoginRequest(@NotBlank @Pattern(regexp=EMAIL_WITH_UNDERSCORE, message="must be a valid email containing _ before @, e.g. demo_user@example.com") String email,@NotBlank String password) {}
    public record OtpRequest(@NotBlank @Pattern(regexp=EMAIL_WITH_UNDERSCORE, message="must be a valid email containing _ before @, e.g. demo_user@example.com") String email) {}
    public record VerifyOtpRequest(@NotBlank @Pattern(regexp=EMAIL_WITH_UNDERSCORE, message="must be a valid email containing _ before @, e.g. demo_user@example.com") String email,@NotBlank String otp) {}
    public record AuthResponse(String token,String message) {}
}
