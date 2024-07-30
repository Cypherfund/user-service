package com.cypherfund.campaign.user.dal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "t_trace_status")
public class TTraceStatus {
    @Id
    @Size(max = 100)
    @Column(name = "lg_trace_status", nullable = false, length = 100)
    private String lgTraceStatus;

    @Column(name = "dt_date_created")
    private Instant dtDateCreated;

    @Size(max = 100)
    @Column(name = "lg_trace_id", length = 100)
    private String lgTraceId;

    @Size(max = 255)
    @Column(name = "str_ext_code")
    private String strExtCode;

    @Size(max = 255)
    @Column(name = "str_external_transaction")
    private String strExternalTransaction;

    @Size(max = 255)
    @Column(name = "str_msg")
    private String strMsg;

    @Size(max = 255)
    @Column(name = "str_status")
    private String strStatus;

}