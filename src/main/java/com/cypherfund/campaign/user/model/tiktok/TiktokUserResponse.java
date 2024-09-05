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
        private String is_verified;
        private String username;
        private String follower_count;
        private String following_count;
        private String likes_count;
        private String video_count;

    }
}
