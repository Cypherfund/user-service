package com.cypherfund.campaign.user.model.payment;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Value;

/**
 * Author: E.Ngai
 * Date: 7/3/2024
 * Time: 11:00 AM
 **/
@Value
public class MobilePaymentDetails {
    String phn;
    @JsonCreator
    public MobilePaymentDetails(String phn) {
        this.phn = phn;
    }

}
