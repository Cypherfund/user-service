package com.cypherfund.campaign.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/validate-token/{token}")
    ResponseEntity<?> validateToken(@PathVariable String token);
}
