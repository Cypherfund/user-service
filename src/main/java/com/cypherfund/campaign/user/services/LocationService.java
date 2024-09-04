package com.cypherfund.campaign.user.services;

import com.cypherfund.campaign.user.model.LocationResponse;
import com.cypherfund.campaign.user.proxies.LocationFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Author: E.Ngai
 * Date: 9/4/2024
 * Time: 1:56 PM
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationFeignClient locationFeignClient;
    @Cacheable(value = "location", key = "#ipAddress")
    public LocationResponse getLocation(String ipAddress) {
        log.info("Getting location for ip address: {}", ipAddress);

        LocationResponse locationResponse = locationFeignClient.getLocation(ipAddress);

        log.info("Location response: {}", locationResponse);
        return locationResponse;

    }
}
