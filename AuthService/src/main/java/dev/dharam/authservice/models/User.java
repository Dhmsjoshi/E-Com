package dev.dharam.authservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_username", columnList = "username")
})
public class User extends BaseModel {
    @NotBlank(message = "Email is mandatory")
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message = "Please provide a valid email address")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Column(nullable = false)
    private String password;

    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(unique = true, length = 50)
    private String username;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    @Column(name = "phone_number", unique = true, length = 20)
    private String phoneNumber;

    @Column(name = "is_email_verified", nullable = false)
    private boolean isEmailVerified = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_roles_user")),
            inverseJoinColumns = @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "fk_user_roles_role"))
    )
    private Set<Role> roles;
}
