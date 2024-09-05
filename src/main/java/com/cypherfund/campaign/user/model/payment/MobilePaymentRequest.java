package com.cypherfund.campaign.user.model.payment;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Author: E.Ngai
 * Date: 3/14/2024
 * Time: 2:28 PM
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class MobilePaymentRequest extends BasePaymentRequest{
    private String phn;
}
