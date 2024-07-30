package com.cypherfund.campaign.user.model;

import com.cypherfund.campaign.user.utils.Enumerations;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CryptoPaymentRequest extends BasePaymentRequest{
    private String receiverAddress;
    private String currency;
    private Enumerations.COINBASE_PRICE_TYPE priceType;
}
