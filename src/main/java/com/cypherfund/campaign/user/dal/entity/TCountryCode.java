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
@Table(name = "t_country_codes")
public class TCountryCode implements Serializable {
    private static final long serialVersionUID = -8447658192235998982L;
    @Id
    @Column(name = "KEY_COUNTRY_CODE", nullable = false, length = 3)
    private String keyCountryCode;

    @Column(name = "str_country_name", length = 50)
    private String strCountryName;

}
