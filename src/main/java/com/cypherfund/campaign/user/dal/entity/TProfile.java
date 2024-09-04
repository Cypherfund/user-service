package com.cypherfund.campaign.user.dal.entity;

import com.cypherfund.campaign.user.utils.Enumerations;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "t_profile")
public class TProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private TUser user;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "social_media_account", nullable = false)
    private Enumerations.SOCIAL_MEDIA_PLATFORM socialMediaAccount;

    @Size(max = 100)
    @NotNull
    @Column(name = "account_name", nullable = false, length = 100)
    private String accountName;

    @Size(max = 100)
    @NotNull
    @Column(name = "account_id", nullable = false, length = 100)
    private String accountId;

    @NotNull
    @Column(name = "dt_created_at", nullable = false)
    private Instant dtCreatedAt;

    @Size(max = 500)
    @Column(name = "img_url", length = 500)
    private String imgUrl;

}