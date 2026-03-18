package dev.dharam.authservice.repository;

import dev.dharam.authservice.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role,UUID> {

    Optional<Role> findByName(String roleName);

    @Override
    Optional<Role> findById(UUID roleId);

    @Override
    List<Role> findAll();

    @Query(value = "SELECT role_id FROM user_roles WHERE user_id = :userId", nativeQuery = true)
    List<Long> findAllRoleIdsByUserId(@Param("userId") Long userId);
}
