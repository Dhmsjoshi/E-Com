package dev.dharam.authservice.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupUserRequestDto(
        @NotBlank(message = "Email is mandatory")
        @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message = "Please provide a valid email address")
        String email,

        @NotBlank(message = "Password cannot be empty")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
                message = "Password must contain one uppercase, one lowercase, one digit and one special character")
        String password,

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Please provide a valid phone number (10-15 digits)")
        String phoneNumber
) {};
