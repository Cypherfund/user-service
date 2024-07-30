package com.cypherfund.campaign.user.model;

import com.cypherfund.campaign.user.utils.Enumerations;
import lombok.Data;

@Data
public class DebitRequest {
    String userId;
    double amount;
    String reference;
    Enumerations.TRANSACTION_TYPE transactionType;
    private String extra;

}
