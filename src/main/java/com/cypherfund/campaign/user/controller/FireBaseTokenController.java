package com.cypherfund.campaign.user.controller;

import com.cypherfund.campaign.user.services.FirebaseTokenConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author: E.Ngai
 * Date: 10/24/2024
 * Time: 1:14 PM
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/firebase-token/{token}")
public class FireBaseTokenController {
    private final FirebaseTokenConfigService firebaseTokenConfigService;

    @PostMapping
    public void saveToken(@PathVariable String token, @PathVariable String userId) {
        firebaseTokenConfigService.saveToken(token, userId);
    }
}
