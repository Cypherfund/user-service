package com.cypherfund.campaign.user.model.tiktok;

import lombok.Data;

/**
 * Author: E.Ngai
 * Date: 9/5/2024
 * Time: 3:02 PM
 **/
@Data
public class TikTokVideoFilter {
    private VideoFilter filters;

    @lombok.Data
    public static class VideoFilter {
        private String[] video_ids;
    }
}
