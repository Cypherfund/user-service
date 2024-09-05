package com.cypherfund.campaign.user.model.tiktok;

import lombok.Data;

/**
 * Author: E.Ngai
 * Date: 9/5/2024
 * Time: 2:33 PM
 **/
@Data
public class TiktokResponse<T> {
    private T data;
    private TiktokError error;
}
