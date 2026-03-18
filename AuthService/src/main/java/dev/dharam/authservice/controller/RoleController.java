package dev.dharam.authservice.controller;

import dev.dharam.authservice.dtos.CreateRoleRequestDto;
import dev.dharam.authservice.dtos.RoleResponseDto;
import dev.dharam.authservice.service.roleservice.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Validated
@Tag(name = "Role Management", description = "Endpoints for managing system authorities and roles")
public class RoleController {
    private final RoleService roleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new role", description = "Adds a new authority (e.g., ADMIN, SELLER) to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Role created successfully"),
            @ApiResponse(responseCode = "409", description = "Conflict: Role already exists")
    })
    public ResponseEntity<RoleResponseDto> createRole(@Valid @RequestBody CreateRoleRequestDto requestDto) {
        return new ResponseEntity<>(roleService.createRole(requestDto.roleName()), HttpStatus.CREATED);
    }

    @GetMapping("/{roleId}")
    @Operation(summary = "Find role by ID", description = "Fetch a specific role's details by its UUID.")
    public ResponseEntity<RoleResponseDto> getRoleById(@PathVariable UUID roleId) {
        return ResponseEntity.ok(roleService.findByRoleId(roleId));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get roles of a user", description = "Lists all roles currently assigned to a specific user.")
    @ApiResponse(responseCode = "200", description = "List of roles returned")
    public ResponseEntity<List<RoleResponseDto>> getRolesByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(roleService.getRolesByUser(userId));
    }

    @GetMapping
    @Operation(summary = "List all roles", description = "Returns a complete list of all roles available in the system.")
    public ResponseEntity<List<RoleResponseDto>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

}
