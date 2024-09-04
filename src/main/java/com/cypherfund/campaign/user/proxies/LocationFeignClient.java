package com.cypherfund.campaign.user.proxies;

import com.cypherfund.campaign.user.model.LocationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Author: E.Ngai
 * Date: 9/4/2024
 * Time: 2:14 PM
 **/
@FeignClient(name = "location-service", url="http://ip-api.com")
public interface LocationFeignClient {
    @GetMapping("/json/{ipAddress}")
    LocationResponse getLocation(@PathVariable("ipAddress") String ipAddress);
}
