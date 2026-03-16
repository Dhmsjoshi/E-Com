package dev.dharam.authservice.controller;

import dev.dharam.authservice.dtos.*;
import dev.dharam.authservice.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
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
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginUserRequestDto requestDto){
        InternalLoginResultDto loginResultDto = authService.login(requestDto.email(), requestDto.password());
        LoginResponseDto responseDto = new LoginResponseDto(loginResultDto.jwtToken(),loginResultDto.userResponseDto());

        ResponseCookie resCookie = ResponseCookie.from("refreshToken", loginResultDto.refreshToken())
                        .httpOnly(true)
                        .secure(false)
                        .path("/")
                        .maxAge(Duration.ofDays(30))
                        .sameSite("Strict")
                        .build();


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookie.toString())
                .body(responseDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue(name = "refreshToken",required = false)
                                             String refreshToken){
        if(refreshToken != null){
            authService.logout(refreshToken);
        }
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body("Logged out successfully");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String,String>> refresh(@CookieValue(name = "refreshToken") String refreshToken){
        String newAccessToken = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(Map.of("accessToken",newAccessToken));
    }
}
