package com.cypherfund.campaign.user.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
@RequestMapping("/oauth2")
public interface GoogleLoginUiResource {
    @PostMapping("authorize/google")
    void initiateGoogleLogin(@RequestBody String body);

    @PostMapping("callback/google")
    void completeGoogleLogin(@RequestBody String body);

    @PostMapping("webhook/google")
    void googleUpdates(@RequestBody String body);
}
