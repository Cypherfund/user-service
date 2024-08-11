package com.cypherfund.campaign.user.model;

import lombok.Data;

@Data
public class TiktokTokenResponse {
    private String access_token;
    private int expires_in;
    private String token_type;
    private String error;
    private String error_description;
    private String log_id;
}