package com.cypherfund.campaign.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class SignUpRequest {
//    @NotBlank
    @Size(min = 4, max = 40)
    private String name;

    @NotBlank
    @Size(min = 3, max = 15)
    private String username;

    @NotBlank
    @Size(max = 40)
    @Email
    private String email;

    private String redirectUrl;

//    @Pattern(regexp="^(\\+\\d{3})\\d{5,16}$", message="incorrect phone number: expected format is +xxx xxxxx")
    @Size(max = 20)
    private String phone;
//    @NotBlank
//    @Size(min = 6, max = 20, message = "password invalid")
    private String password;

//    @NotNull
//    @NotEmpty(message="assign at least one role to the user")
    private List<String> roles;

}
