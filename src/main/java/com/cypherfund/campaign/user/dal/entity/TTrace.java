package com.cypherfund.campaign.user.dal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "t_trace")
public class TTrace {
    @Id
    @Size(max = 100)
    @Column(name = "lg_trace_id", nullable = false, length = 100)
    private String lgTraceId;

    @Column(name = "db_amount")
    private Double dbAmount;

    @Column(name = "dt_date_created")
    private Instant dtDateCreated;

    @Size(max = 255)
    @Column(name = "str_originating_transaction")
    private String strOriginatingTransaction;

    @Size(max = 255)
    @Column(name = "str_phone_number")
    private String strPhoneNumber;

    @Size(max = 10)
    @Column(name = "str_provider_code", length = 10)
    private String strProviderCode;

    @Size(max = 50)
    @NotNull
    @Column(name = "lg_user_id", nullable = false, length = 50)
    private String lgUserId;

    @Size(max = 15)
    @Column(name = "str_type", length = 15)
    private String strType;

}