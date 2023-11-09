package com.cypherfund.campaign.user.model;

import lombok.Value;

@Value
public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private String userId;

    public JwtAuthenticationResponse(String accessToken, String userId) {
        this.accessToken = accessToken;
        this.userId = userId;
    }
}
