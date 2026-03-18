package dev.dharam.authservice.service.userservice;

import dev.dharam.authservice.dtos.RoleResponseDto;
import dev.dharam.authservice.dtos.UserResponseDto;
import dev.dharam.authservice.exception.ResourceNotFoundException;
import dev.dharam.authservice.mapper.DtoMapper;
import dev.dharam.authservice.models.Role;
import dev.dharam.authservice.models.User;
import dev.dharam.authservice.repository.RoleRepository;
import dev.dharam.authservice.repository.UserRepository;
import dev.dharam.authservice.service.roleservice.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DtoMapper dtoMapper;

    @Override
    public UserResponseDto getUserById(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(  "User not found"));
        return UserResponseDto.from(user);
    }

    @Override
    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with this email not found"));
        return UserResponseDto.from(user);
    }

    @Override
    @Transactional
    public UserResponseDto assignRole(UUID userId, UUID roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        user.getRoles().add(role);
        return UserResponseDto.from(userRepository.save(user));
    }
}
