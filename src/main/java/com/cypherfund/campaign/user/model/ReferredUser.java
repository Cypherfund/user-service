package com.cypherfund.campaign.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: E.Ngai
 * Date: 10/25/2024
 * Time: 3:02 PM
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferredUser {
    private String username;
    private boolean completed;
    private int coins;
}
