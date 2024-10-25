package com.cypherfund.campaign.user.controller;

import com.cypherfund.campaign.user.model.JwtAuthenticationResponse;
import com.cypherfund.campaign.user.model.tiktok.TiktokUserResponse;
import com.cypherfund.campaign.user.model.tiktok.TiktokVideoListResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/oauth2")
public interface TiktokLoginUiResource {
    @GetMapping("/authorize/tiktok")
    ResponseEntity initiateTiktokLogin(HttpServletResponse response,
                                       @RequestParam(required = false) String redirectUrl,
                                       @RequestParam(required = false) String referralCode);

    @GetMapping("/callback/tiktok")
    ResponseEntity<JwtAuthenticationResponse> completeTiktokLogin(@RequestParam(value = "code", required = false) String code,
                             @RequestParam(value = "state", required = false) String state,
                             @RequestParam(value = "scopes", required = false) String scopes,
                             @RequestParam(value = "error", required = false) String error,
                             @RequestParam(value = "error_description", required = false) String errorDescription,
                             @CookieValue(value = "csrfState", required = false) String csrfState);

    @PostMapping("webhook/tiktok")
    void tiktokUpdates(@RequestBody String body);

    // New endpoint to get TikTok user profile information
    @GetMapping("/tiktok/user/info")
    ResponseEntity<TiktokUserResponse> getTiktokUserInfo(@RequestParam String userId);

    // New endpoint to get TikTok user's recent videos
    @GetMapping("/tiktok/user/videos")
    ResponseEntity<TiktokVideoListResponse> getTiktokUserVideos(@RequestParam String userId);

    // New endpoint to query TikTok user's videos by video IDs
    @GetMapping("/tiktok/user/videos/query")
    ResponseEntity<TiktokVideoListResponse> queryTiktokUserVideos(@RequestParam String userId, @RequestParam String[] videoIds);
}
