package com.cypherfund.campaign.user.model;

import lombok.Data;

@Data
public class TiktokUserResponse {
    private Data data;
    private Error error;

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

    }

    @lombok.Data
    public static class Error {
        private String code;
        private String message;
        private String log_id;
    }
}