package com.cypherfund.campaign.user.controller;

import com.cypherfund.campaign.user.model.ApiResponse;
import com.cypherfund.campaign.user.model.LocationResponse;
import com.cypherfund.campaign.user.services.LocationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Author: E.Ngai
 * Date: 9/4/2024
 * Time: 1:55 PM
 **/
@Slf4j
@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @GetMapping("/{ipAddress}")
    public ResponseEntity<ApiResponse<LocationResponse>> getLocation(@PathVariable String ipAddress) {
        return ResponseEntity.ok(new ApiResponse<>(true, "", locationService.getLocation(ipAddress)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<LocationResponse>> getLocationWithoutIp(HttpServletRequest request,
                                                                              @RequestHeader(value = "X-Forwarded-For", required = false) String xForwardedFor) {
        String ipAddress;

        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            log.info("X-Forwarded-For: {}", xForwardedFor);
            // If there is a X-Forwarded-For header, use the first IP in the list
            ipAddress = xForwardedFor.split(",")[0];
        } else {
            // Otherwise, use the remote address from the request
            ipAddress = request.getRemoteAddr();
        }

        log.info("IP Address: {}", ipAddress);
        if (ipAddress.equals("127.0.0.1")) {
            return ResponseEntity.ok(new ApiResponse<>(true, "", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "", locationService.getLocation(ipAddress)));
    }

}
