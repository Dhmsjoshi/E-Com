package dev.dharam.authservice.oAuth2.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.dharam.authservice.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SecurityUser implements UserDetails, Serializable {

    private static final long serialVersionUID = 1L; // Serialization compatibility ke liye

    private UUID id;
    private String email;
    private String password;
    private List<SimpleGrantedAuthority> authorities;

    // 1. No-args constructor (Jackson ke liye mandatory hai)
    public SecurityUser() {}

    public SecurityUser(User user, List<SimpleGrantedAuthority> authorities) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.authorities = authorities;
    }

    // Standard UserDetails methods
    @Override
    public String getUsername() { return this.email; }

    @Override
    public String getPassword() { return this.password; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return this.authorities; }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }

    // Getters for id
    public UUID getId() { return id; }
}