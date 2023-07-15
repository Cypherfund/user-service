package com.cypherfund.campaign.user.dal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "t_paramters")
public class TParamter implements Serializable {
    private static final long serialVersionUID = -4543263880803128892L;
    @Id
    @Column(name = "str_key", nullable = false)
    private String strKey;

    @Column(name = "str_value")
    private String strValue;

    @Column(name = "str_description")
    private String strDescription;

    @Column(name = "b_status")
    private Boolean bStatus;

}
