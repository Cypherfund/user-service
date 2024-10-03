package com.cypherfund.campaign.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendNotificationDto {
    private String title;
    private String message;
    private String imageUrl;
    private String type;
    private String channel;
    private String[] emailRecipients;
}
