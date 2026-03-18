package dev.dharam.authservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role extends BaseModel{
    @NotBlank(message = "Role name cannot be empty")
    @Pattern(regexp = "^ROLE_[A-Z]+$", message = "Role name must start with ROLE_ and be in uppercase")
    @Column(nullable = false, unique = true, length = 50)
    private String name; // e.g., ROLE_ADMIN, ROLE_CUSTOMER

    @Column(length = 255)
    private String description;


}
