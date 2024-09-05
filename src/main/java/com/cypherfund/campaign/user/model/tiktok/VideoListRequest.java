package com.cypherfund.campaign.user.model.tiktok;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Author: E.Ngai
 * Date: 9/5/2024
 * Time: 2:29 PM
 **/
@Value
@AllArgsConstructor
public class VideoListRequest {
    private int max_count;
}
