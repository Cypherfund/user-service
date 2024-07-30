package com.cypherfund.campaign.user.model;

import com.cypherfund.campaign.user.utils.Enumerations;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private Enumerations.PAYMENT_STATUS status;
    private String transactionId;
    private Object data;
}
