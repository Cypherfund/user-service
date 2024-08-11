package com.cypherfund.campaign.user.controller;

import com.cypherfund.campaign.user.model.JwtAuthenticationResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth2")
public interface TiktokLoginUiResource {
    @GetMapping("/authorize/tiktok")
    ResponseEntity initiateTiktokLogin(HttpServletResponse response,
                                       @RequestParam String username,
                                       @RequestParam String email);

    @GetMapping("/callback/tiktok")
    ResponseEntity<JwtAuthenticationResponse> completeTiktokLogin(@RequestParam(value = "code", required = false) String code,
                             @RequestParam(value = "state", required = false) String state,
                             @RequestParam(value = "scopes", required = false) String scopes,
                             @RequestParam(value = "error", required = false) String error,
                             @RequestParam(value = "error_description", required = false) String errorDescription,
                             @CookieValue(value = "csrfState", required = false) String csrfState);

    @PostMapping("webhook/tiktok")
    void tiktokUpdates(@RequestBody String body);
}
