package dev.dharam.authservice.dtos;

public record InternalLoginResultDto(String jwtToken, String refreshToken, UserResponseDto userResponseDto) {
}
