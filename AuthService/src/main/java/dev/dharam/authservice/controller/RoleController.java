package dev.dharam.authservice.controller;

import dev.dharam.authservice.dtos.CreateRoleRequestDto;
import dev.dharam.authservice.dtos.RoleResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    @PostMapping
    public ResponseEntity<RoleResponseDto> createRole(@RequestBody CreateRoleRequestDto requestDto){
        return null;
    }

    @GetMapping
    public ResponseEntity<List<RoleResponseDto>> getRolesByUser(Long userId){
        return null;
    }
}
