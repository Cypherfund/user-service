package com.cypherfund.campaign.user.model.payment;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RechargeCoinRequest extends RechargeRequest{
    private double coinAmount;
}
