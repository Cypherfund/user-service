package com.cypherfund.campaign.user.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Author: E.Ngai
 * Date: 10/25/2024
 * Time: 3:02 PM
 **/
@Builder
@Data
public class ReferredUser {
    private String username;
    private boolean completed;
    private BigDecimal coins;
}
