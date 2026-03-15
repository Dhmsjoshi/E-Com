package dev.dharam.authservice.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "sessions")
public class Session extends BaseModel{
    @Column(nullable = false, length = 512, unique = true)
    private String refreshToken;

    // Instant use kar rahe hain (Production Standard)
    @Column(name = "expiring_at", nullable = false)
    private Instant expiringAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_session_user"))
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SessionStatus status;


}
