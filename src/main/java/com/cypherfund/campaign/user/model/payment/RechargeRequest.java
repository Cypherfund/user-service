package com.cypherfund.campaign.user.model.payment;

import com.cypherfund.campaign.user.utils.Enumerations.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RechargeRequest {
    @NotNull(message = "user cannot not be null or empty")
    private String userId;
    @NotNull
    private String reference;
    private PaymentMethod paymentMethod;
    private String paymentCode;
    private String extra;
    private double amount;
    private String callbackUrl;
}
