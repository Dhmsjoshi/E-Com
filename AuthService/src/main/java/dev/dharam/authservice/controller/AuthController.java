package dev.dharam.authservice.controller;

import dev.dharam.authservice.dtos.LoginResponseDto;
import dev.dharam.authservice.dtos.LoginUserRequestDto;
import dev.dharam.authservice.dtos.SignupUserRequestDto;
import dev.dharam.authservice.dtos.UserResponseDto;
import dev.dharam.authservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@RequestBody SignupUserRequestDto requestDto){
        return ResponseEntity.ok(authService.signup(requestDto.email(), requestDto.password()));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody LoginUserRequestDto requestDto){
        LoginResponseDto dto = authService.login(requestDto.email(), requestDto.password());
        String token = dto.token();
        UserResponseDto user = dto.userResponseDto();

       MultiValueMapAdapter headers = new MultiValueMapAdapter(new HashMap());
        headers.add("Authorization", "Bearer " + token);

        return new ResponseEntity<>(user,headers,HttpStatus.OK);
    }

//    @PostMapping
//    public ResponseEntity<UserResponseDto> validate(@RequestBody SignupUserRequestDto requestDto){
//        return null;
//    }
}
