package dev.dharam.authservice.dtos;

import dev.dharam.authservice.models.Role;

import java.util.UUID;

public record RoleResponseDto(UUID roleId, String roleName) {

    public static RoleResponseDto from(Role role) {
        return new RoleResponseDto(role.getId(), role.getName());
    }
};
