package com.cypherfund.campaign.user.controller;

import com.cypherfund.campaign.user.model.ApiResponse;
import com.cypherfund.campaign.user.model.LocationResponse;
import com.cypherfund.campaign.user.services.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Author: E.Ngai
 * Date: 9/4/2024
 * Time: 1:55 PM
 **/
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @GetMapping("/{ipAddress}")
    public ResponseEntity<ApiResponse<LocationResponse>> getLocation(@PathVariable String ipAddress) {
        return ResponseEntity.ok(new ApiResponse<>(true, "", locationService.getLocation(ipAddress)));
    }

}
