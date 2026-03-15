package dev.dharam.authservice.dtos;

import dev.dharam.authservice.models.Role;
import org.springframework.util.MultiValueMapAdapter;

import java.util.Set;

public record LoginResponseDto(String jwtToken,String refreshToken,UserResponseDto userResponseDto) {};
