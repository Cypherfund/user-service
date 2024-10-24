package com.cypherfund.campaign.user.dal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "t_users")
public class TUser implements Serializable {
    private static final long serialVersionUID = 1810721705496824031L;
    @Id
    @Size(max = 50)
    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @Size(max = 255)
    @NotNull
    @Column(name = "username", nullable = false)
    private String username;

    @Size(max = 15)
    @Column(name = "phone", length = 15)
    private String phone;

    @Size(max = 255)
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @Size(max = 15)
    @Column(name = "status", length = 15)
    private String status;

    @Column(name = "dt_created")
    private Instant dtCreated;

    @Column(name = "dt_updated")
    private Instant dtUpdated;

    @Size(max = 40)
    @Column(name = "email", length = 40)
    private String email;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private List<TRoleUser> lgRole;

    @Size(max = 15)
    @Column(name = "str_login_provider", length = 15)
    private String strLoginProvider;

    @Column(name = "img_url")
    private String imgUrl;

    @Size(max = 10)
    @Column(name = "referrer", length = 10)
    private String referrer;

    @Size(max = 5)
    @Column(name = "referral_code", length = 5)
    private String referralCode;

}
