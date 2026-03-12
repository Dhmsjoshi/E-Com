package dev.dharam.authservice.dtos;

import dev.dharam.authservice.models.Role;
import dev.dharam.authservice.models.User;

import java.util.Set;
import java.util.UUID;

public record UserResponseDto(UUID userId, String email, Set<Role> roles) {

    public static  UserResponseDto from(User user){
        return new UserResponseDto(user.getId(), user.getEmail(), user.getRoles());
    }
};
