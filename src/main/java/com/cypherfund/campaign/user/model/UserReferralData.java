package com.cypherfund.campaign.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Author: E.Ngai
 * Date: 10/25/2024
 * Time: 2:37 PM
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserReferralData {
    private String referralCode;
    private String referralLink;
    private List<ReferredUser> referredUsers;

}
