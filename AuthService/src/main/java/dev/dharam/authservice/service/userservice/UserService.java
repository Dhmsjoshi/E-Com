package dev.dharam.authservice.service.userservice;

import dev.dharam.authservice.dtos.UserResponseDto;

import java.util.UUID;

public interface UserService {
    UserResponseDto getUserById(UUID userId);
    UserResponseDto getUserByEmail(String email);
    UserResponseDto assignRole(UUID userId, UUID roleId);
}
