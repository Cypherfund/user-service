package com.cypherfund.campaign.user.dal.entity;

import com.cypherfund.campaign.user.utils.Enumerations;
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
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "user_id")
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Enumerations.TRANSACTION_TYPE type;

    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "created_at")
    private Instant createdAt;

    @Size(max = 50)
    @NotNull
    @Column(name = "reference", nullable = false, length = 50)
    private String reference;

}