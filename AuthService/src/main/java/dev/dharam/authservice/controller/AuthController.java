package dev.dharam.authservice.controller;

import dev.dharam.authservice.dtos.*;
import dev.dharam.authservice.service.authservice.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Validated // Path variables ya Cookie validation ke liye
@Tag(name = "Authentication Management", description = "Endpoints for User Signup, Login, Logout and Token Refresh")
public class AuthController {

    private final AuthService authService;


    @PostMapping("/signup")
    @Operation(summary = "Register a new user", description = "Creates a new user account with email and password validation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., weak password or invalid email)"),
            @ApiResponse(responseCode = "409", description = "User with this email already exists")
    })
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody SignupUserRequestDto requestDto){
        return ResponseEntity.ok(authService.signup(requestDto.email(), requestDto.password()));
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticates user and returns a JWT access token in body and a Refresh Token in an HttpOnly cookie.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginUserRequestDto requestDto){
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
    @Operation(summary = "User Logout", description = "Invalidates the refresh token and clears the authentication cookie.")
    @ApiResponse(responseCode = "200", description = "Logged out successfully")
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
    @Operation(summary = "Refresh Access Token", description = "Generates a new short-lived Access Token using the Refresh Token from the cookie.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "403", description = "Refresh token is invalid or expired")
    })
    public ResponseEntity<Map<String,String>> refresh(@CookieValue(name = "refreshToken") String refreshToken){
        String newAccessToken = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(Map.of("accessToken",newAccessToken));
    }
}
