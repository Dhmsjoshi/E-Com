package dev.dharam.authservice.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "sessions")
public class Session extends BaseModel{
    @Column(nullable = false, length = 1000)
    private String token;

    // LocalDateTime use karna zyada modern aur asaan hai
    @Column(name = "expiring_at", nullable = false)
    private LocalDateTime expiringAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_session_user"))
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20) // Length de dena achha hai enums ke liye
    private SessionStatus status;


}
