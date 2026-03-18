package dev.dharam.authservice.service.roleservice;

import dev.dharam.authservice.dtos.RoleResponseDto;
import dev.dharam.authservice.exception.ResourceAlreadyExistsException;
import dev.dharam.authservice.exception.ResourceNotFoundException;
import dev.dharam.authservice.mapper.DtoMapper;
import dev.dharam.authservice.models.Role;
import dev.dharam.authservice.models.User;
import dev.dharam.authservice.repository.RoleRepository;
import dev.dharam.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService{
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final DtoMapper dtoMapper;

    @Override
    public RoleResponseDto createRole(String roleName) {
        roleRepository.findByName(roleName).ifPresent(role -> {
            throw new ResourceAlreadyExistsException("Role " + roleName + " already exists");
        });
        Role role = new Role();
        role.setName(roleName.toUpperCase());
        Role savedRole = roleRepository.save(role);
        return RoleResponseDto.from(savedRole);
    }

    @Override
    public RoleResponseDto findByRoleId(UUID roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(
                ()-> new ResourceNotFoundException("Role with id " + roleId + " not found")
        );
        return RoleResponseDto.from(role);
    }

    @Override
    public List<RoleResponseDto> getRolesByUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                ()->new ResourceNotFoundException("User with id: "+userId+" not found!")
        );
        return user.getRoles().stream().map(RoleResponseDto::from).collect(Collectors.toList());
    }

    @Override
    public List<RoleResponseDto> getAllRoles() {
        return roleRepository.findAll().stream().map(RoleResponseDto::from).collect(Collectors.toList());
    }
}
