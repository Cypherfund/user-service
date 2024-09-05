package com.cypherfund.campaign.user.model.tiktok;

import lombok.Data;

import java.util.List;

/**
 * Author: E.Ngai
 * Date: 9/5/2024
 * Time: 2:15 PM
 **/
@Data
public class TiktokVideoListResponse {
    private TiktokVideoQueryData data;
    private TiktokError error;

    @lombok.Data
    public static class TiktokVideoQueryData {
        private List<TikTokVideo> videos;
        private long cursor;
        private boolean has_more;
    }

    @lombok.Data
    public static class TikTokVideo {
        private String cover_image_url;
        private String id;
        private String title;
        private String embed_html;
        private String embed_link;
        private int like_count;
        private int comment_count;
        private int share_count;
        private long view_count;
        private long share_url;

    }

}
