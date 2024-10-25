package com.cypherfund.campaign.user.model;

import lombok.Data;

import java.util.List;

/**
 * Author: E.Ngai
 * Date: 10/25/2024
 * Time: 2:37 PM
 **/

@Data
public class UserReferralData {
    private String referralCode;
    private String referralLink;
    private List<ReferredUser> referredUsers;

}
