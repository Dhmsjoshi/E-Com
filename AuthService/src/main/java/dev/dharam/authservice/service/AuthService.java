package dev.dharam.authservice.service;

import dev.dharam.authservice.dtos.LoginResponseDto;
import dev.dharam.authservice.dtos.UserResponseDto;

public interface AuthService {

    UserResponseDto signup(String email, String password);
    LoginResponseDto login(String email, String password);
    void logout(String refreshToken);
    String refreshAccessToken(String refreshToken);

}
