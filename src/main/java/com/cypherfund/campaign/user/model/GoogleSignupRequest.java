package com.cypherfund.campaign.user.model;

import lombok.Data;

@Data
public class GoogleSignupRequest {
    private String name;
    private String id;
    private String username;
    private String email;
    private String phone;
    private String password;
    private String imageUrl;
}