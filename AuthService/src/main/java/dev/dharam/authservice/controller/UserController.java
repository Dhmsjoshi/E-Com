package dev.dharam.authservice.controller;

import dev.dharam.authservice.dtos.LoginUserRequestDto;
import dev.dharam.authservice.dtos.UserResponseDto;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserDetails(Long userId){
        return null;
    }
}
