package com.cypherfund.campaign.user.controller;

import com.cypherfund.campaign.user.model.JwtAuthenticationResponse;
import com.cypherfund.campaign.user.model.LoginRequest;
import com.cypherfund.campaign.user.model.SignUpRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
public interface LoginUiResource {

    @PostMapping("/signin")
    ResponseEntity<JwtAuthenticationResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest);

    @PostMapping("/signup")
    ResponseEntity<?> registerUser( @Valid @RequestBody SignUpRequest signUpRequest);
}
