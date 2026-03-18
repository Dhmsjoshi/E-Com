package dev.dharam.authservice.controller;

import dev.dharam.authservice.dtos.UserResponseDto;
import dev.dharam.authservice.service.userservice.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for user profile and role assignment")
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId.toString() == authentication.name")
    @Operation(summary = "Get user by ID", description = "Fetches user profile details using their unique UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponseDto> getUserDetails(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user by Email", description = "Retrieves user details based on their registered email address.")
    @ApiResponse(responseCode = "200", description = "User found")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable @Email(message = "Invalid email format") String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PostMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign role to user", description = "Links a specific role to a user. Useful for elevating permissions.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role successfully assigned"),
            @ApiResponse(responseCode = "404", description = "Either User or Role not found")
    })
    public ResponseEntity<UserResponseDto> assignRoleToUser(
            @PathVariable UUID userId,
            @PathVariable UUID roleId) {
        return ResponseEntity.ok(userService.assignRole(userId, roleId));
    }

}
