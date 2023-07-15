package com.cypherfund.campaign.user.dal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "t_role", schema = "users")
public class TRole implements Serializable {
    private static final long serialVersionUID = -4540703815422409734L;
    @Id
    @Size(max = 20)
    @Column(name = "lg_role_id", nullable = false, length = 20)
    private String lgRoleId;

    @Size(max = 50)
    @Column(name = "str_role_description", length = 50)
    private String strRoleDescription;

    @Column(name = "b_active")
    private boolean bActive;

    @NotNull
    @Column(name = "dt_created", nullable = false)
    private Instant dtCreated;

}
