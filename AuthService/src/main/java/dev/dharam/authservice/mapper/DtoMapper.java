package dev.dharam.authservice.mapper;

import dev.dharam.authservice.dtos.RoleResponseDto;
import dev.dharam.authservice.dtos.UserResponseDto;
import dev.dharam.authservice.models.Role;
import dev.dharam.authservice.models.User;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DtoMapper {

    public  Role toRoleEntity(RoleResponseDto dto){
        Role role = new Role();
        role.setId(dto.roleId());
        role.setName(dto.roleName());
        return role;
    }

    public User toUserEntity(UserResponseDto dto){
        User user = new User();
        user.setId(dto.userId());
        Set<Role> roles = dto.roles().stream()
                .map(name -> {
                    Role role = new Role();
                    role.setName(name);
                    return role;
                })
                .collect(Collectors.toSet());

        user.setRoles(roles);
        user.setEmail(dto.email());
        return user;
    }
}
