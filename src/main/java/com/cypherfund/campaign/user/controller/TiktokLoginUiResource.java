package com.cypherfund.campaign.user.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/oauth2")
public interface TiktokLoginUiResource {
    @PostMapping("authorize/tiktok")
    void initiateTiktokLogin(@RequestBody String body);

    @PostMapping("callback/tiktok")
    void completeTiktokLogin(@RequestBody String body);

    @PostMapping("webhook/tiktok")
    void tiktokUpdates(@RequestBody String body);
}
