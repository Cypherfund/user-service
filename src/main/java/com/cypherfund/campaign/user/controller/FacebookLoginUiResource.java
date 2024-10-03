package com.cypherfund.campaign.user.controller;//package com.cypherfund.campaign.user.ui;

import com.cypherfund.campaign.user.model.ApiResponse;
import com.cypherfund.campaign.user.model.FacebookSignupRequest;
import com.cypherfund.campaign.user.model.JwtAuthenticationResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth2")
public interface FacebookLoginUiResource {
    @PostMapping("/authorize/facebook")
    void initiateFacebookLogin(@RequestBody String body);

    @PostMapping("/grant/facebook")
    ApiResponse<JwtAuthenticationResponse> initiateFacebookLoginWithCode(@RequestParam String code);

    @PostMapping("/callback/facebook")
    ResponseEntity<JwtAuthenticationResponse> completeFacebookLogin(@RequestBody @Valid FacebookSignupRequest body);

    @PostMapping("/webhook/facebook")
    void facebookUpdates(@RequestBody String body);
}
