package com.cypherfund.campaign.user.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String usernameOrEmailOrPhone;

    @NotBlank
    private String password;
}
