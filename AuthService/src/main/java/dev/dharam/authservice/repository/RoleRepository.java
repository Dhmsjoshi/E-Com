package dev.dharam.authservice.repository;

import dev.dharam.authservice.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
}
