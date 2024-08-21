package com.cypherfund.campaign.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for {@link com.cypherfund.campaign.user.dal.entity.TAccountBalance}
 */
@Data
public class TAccountBalanceDto implements Serializable {
    private BigDecimal dbCoinBalance;
    Long id;
    @NotNull
    @Size(max = 50)
    String lgUserId;
    @NotNull
    BigDecimal dCurBalance;
    @NotNull
    BigDecimal dWinBalance;
    @NotNull
    Instant dtCreated;
    @NotNull
    Instant dtUpdated;
}