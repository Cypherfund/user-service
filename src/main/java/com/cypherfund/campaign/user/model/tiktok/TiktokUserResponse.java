package com.cypherfund.campaign.user.model.tiktok;

import lombok.Data;

@Data
public class TiktokUserResponse {
    private Data data;
    private TiktokError error;

    @lombok.Data
    public static class Data {
        private User user;
    }

    @lombok.Data
    public static class User {
        private String avatar_url;
        private String open_id;
        private String union_id;
        private String display_name;
        private String bio_description;
        private String profile_deep_link;
        private boolean is_verified;
        private String username;
        private int follower_count;
        private int following_count;
        private int likes_count;
        private int video_count;

    }
}
