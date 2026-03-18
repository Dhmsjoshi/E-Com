package dev.dharam.authservice.service.authservice;

import dev.dharam.authservice.dtos.InternalLoginResultDto;
import dev.dharam.authservice.dtos.UserResponseDto;

public interface AuthService {

    UserResponseDto signup(String email, String password);
    InternalLoginResultDto login(String email, String password);
    void logout(String refreshToken);
    String refreshAccessToken(String refreshToken);

}
