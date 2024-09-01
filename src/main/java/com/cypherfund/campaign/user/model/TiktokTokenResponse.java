package com.cypherfund.campaign.user.model;

import lombok.Data;

@Data
public class TiktokTokenResponse {
    private String access_token;
    private int expires_in; //seconds
    private String token_type;
    private String error;
    private String error_description;
    private String log_id;
    private String scope;
    private String refresh_token;
    private int refresh_expires_in; //seconds
    private String open_id;
}