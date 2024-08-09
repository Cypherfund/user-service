package com.cypherfund.campaign.user.dal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "t_account_balance")
public class TAccountBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lg_account_balance_id", nullable = false)
    private Long id;

    @Size(max = 50)
    @NotNull
    @Column(name = "lg_user_id", nullable = false, length = 50)
    private String lgUserId;

    @NotNull
    @Column(name = "d_cur_balance", nullable = false, precision = 10, scale = 2)
    private BigDecimal dCurBalance;

    @NotNull
    @Column(name = "d_win_balance", nullable = false, precision = 10, scale = 2)
    private BigDecimal dWinBalance;

    @NotNull
    @Column(name = "dt_created", nullable = false)
    private Instant dtCreated;

    @NotNull
    @Column(name = "dt_updated", nullable = false)
    private Instant dtUpdated;

    @Column(name = "db_coin_balance", precision = 10)
    private BigDecimal dbCoinBalance;

}