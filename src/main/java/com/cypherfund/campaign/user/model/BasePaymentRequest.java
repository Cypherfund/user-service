package com.cypherfund.campaign.user.model;

import lombok.Data;

/**
 * Author: E.Ngai
 * Date: 3/14/2024
 * Time: 2:28 PM
 **/
@Data
public class BasePaymentRequest {
    private String currency;
    private double amt;
    private String ref;
    private String desc;
    private String method;
    private String code;
    private String callbackUrl;
}
