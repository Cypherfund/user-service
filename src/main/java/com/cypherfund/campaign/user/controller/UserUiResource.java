package com.cypherfund.campaign.user.controller;

import com.cypherfund.campaign.user.dto.TProfileDto;
import com.cypherfund.campaign.user.model.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("users")
public interface UserUiResource {
    @GetMapping
    ResponseEntity<?> getAllUsers(@RequestParam("page") int page,
                                  @RequestParam("limit") int limit);
    @GetMapping("/id/{id}")
    ResponseEntity<?> getUserbyID(@PathVariable String id);
    @GetMapping("/{username}")
    ResponseEntity<?> getUser(@PathVariable String username);

    @GetMapping("/valid/{username}")
    ResponseEntity<?> validUsername(@PathVariable String username);

    @PostMapping("/validate-token")
    ResponseEntity<?> validateToken(@RequestBody String token);

    //get user profiles
    @GetMapping("/profiles/{userId}")
    ResponseEntity<ApiResponse<List<TProfileDto>>> getUserProfiles(@PathVariable String userId);

    @GetMapping("/{userId}/referrer-code")
    ResponseEntity<ApiResponse<String>> getUserReferralCode(@PathVariable String userId);
}
