package dev.dharam.authservice.controller;

import dev.dharam.authservice.dtos.LoginResponseDto;
import dev.dharam.authservice.dtos.LoginUserRequestDto;
import dev.dharam.authservice.dtos.SignupUserRequestDto;
import dev.dharam.authservice.dtos.UserResponseDto;
import dev.dharam.authservice.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@RequestBody SignupUserRequestDto requestDto){
        return ResponseEntity.ok(authService.signup(requestDto.email(), requestDto.password()));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginUserRequestDto requestDto,
                                                  HttpServletResponse response){
        LoginResponseDto  loginResponseDto = authService.login(requestDto.email(), requestDto.password());

        Cookie cookie = new Cookie("refreshToken", loginResponseDto.refreshToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(30*24*60*60); //30 days

        response.addCookie(cookie);

        return ResponseEntity.ok(loginResponseDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue(name = "refreshToken",required = false)
                                             String refreshToken,
                                         HttpServletResponse response){
        if(refreshToken != null){
            authService.logout(refreshToken);
        }
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String,String>> refresh(@CookieValue(name = "refreshToken") String refreshToken){
        String newAccessToken = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(Map.of("accessToken",newAccessToken));
    }
}
