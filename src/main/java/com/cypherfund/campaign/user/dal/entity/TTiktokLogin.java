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
@Table(name = "t_tiktok_login", schema = "users")
public class TTiktokLogin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "access_token", nullable = false)
    private String accessToken;

    @NotNull
    @Column(name = "expires_in", nullable = false)
    private Integer expiresIn;

    @Size(max = 255)
    @NotNull
    @Column(name = "open_id", nullable = false)
    private String openId;

    @NotNull
    @Column(name = "refresh_expires_in", nullable = false)
    private Integer refreshExpiresIn;

    @Size(max = 255)
    @NotNull
    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Size(max = 255)
    @NotNull
    @Column(name = "scope", nullable = false)
    private String scope;

    @Size(max = 255)
    @NotNull
    @Column(name = "token_type", nullable = false)
    private String tokenType;

    @NotNull
    @Column(name = "dt_created_at", nullable = false)
    private Instant dtCreatedAt;

    @Size(max = 50)
    @Column(name = "user_id", length = 50)
    private String userId;

}