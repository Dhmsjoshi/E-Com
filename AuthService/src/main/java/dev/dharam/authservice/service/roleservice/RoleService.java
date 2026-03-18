package dev.dharam.authservice.service.roleservice;

import dev.dharam.authservice.dtos.CreateRoleRequestDto;
import dev.dharam.authservice.dtos.RoleResponseDto;

import java.util.List;
import java.util.UUID;

public interface RoleService {
    RoleResponseDto createRole(String roleName);
    List<RoleResponseDto> getRolesByUser(UUID userId);
    List<RoleResponseDto> getAllRoles();
    RoleResponseDto findByRoleId(UUID roleId);
}
