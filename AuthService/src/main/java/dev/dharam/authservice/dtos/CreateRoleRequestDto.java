package dev.dharam.authservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateRoleRequestDto(
        @NotBlank(message = "Role name cannot be empty")
        @Size(min = 4, max = 20, message = "Role name should be between 4 and 20 characters")
        @Pattern(regexp = "^[A-Z]+$", message = "Role name must start with 'ROLE_' followed by uppercase letters")
        String roleName
) {};
