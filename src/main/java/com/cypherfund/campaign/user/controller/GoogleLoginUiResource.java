package com.cypherfund.campaign.user.controller;

import com.cypherfund.campaign.user.model.ApiResponse;
import com.cypherfund.campaign.user.model.GoogleSignupRequest;
import com.cypherfund.campaign.user.model.JwtAuthenticationResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/oauth2")
public interface GoogleLoginUiResource {
    @PostMapping("/authorize/google")
    void initiateGoogleLogin(@RequestBody String body);

    @PostMapping("/grant/google")
    ApiResponse<JwtAuthenticationResponse> initiateGoogleLoginWithCode(@RequestParam String code);

    @PostMapping("/callback/google")
    ResponseEntity<JwtAuthenticationResponse> completeGoogleLogin(@RequestBody @Valid GoogleSignupRequest body);

    @PostMapping("/webhook/google")
    void googleUpdates(@RequestBody String body);
}
