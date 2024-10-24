package com.cypherfund.campaign.user.dal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "t_firebase_token")
public class TFirebaseToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @NotNull
    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Size(max = 255)
    @NotNull
    @Column(name = "firebase_token", nullable = false)
    private String firebaseToken;

    @NotNull
    @Column(name = "dt_created_at", nullable = false)
    private Instant dtCreatedAt;

    public TFirebaseToken(String token, String userId) {
        this.firebaseToken = token;
        this.userId = userId;
        this.dtCreatedAt = Instant.now();
    }
}
