package com.cypherfund.campaign.user.model;

import lombok.Data;

/**
 * Author: E.Ngai
 * Date: 9/4/2024
 * Time: 1:57 PM
 **/

@Data
public class LocationResponse {
    private String status;
    private String country;
    private String countryCode;
    private String region;
    private String regionName;
    private String city;
    private String zip;
    private double lat;
    private double lon;
    private String timezone;
    private String isp;
    private String org;
    private String as;
    private String query;
}
