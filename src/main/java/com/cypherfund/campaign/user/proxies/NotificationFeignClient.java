package com.cypherfund.campaign.user.proxies;

import com.cypherfund.campaign.user.dto.SendNotificationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "notification-service", url="${app.notification.endpoint}")
public interface NotificationFeignClient {
    @PostMapping("/notif-api/send/push")
    void pushClientNotification(@RequestBody SendNotificationDto sendNofication,
                                @RequestParam("deviceToken") String deviceToken);
    @PostMapping("/notif-api/send/sms")
    void sendSmsNotification(@RequestBody SendNotificationDto sendNotificationDto);

    @PostMapping("/notif-api/send/email")
    void sendEmailNotification(@RequestBody SendNotificationDto sendNotificationDto);

    @PostMapping("/notif-api/send/email")
    void sendRealtime(@RequestBody SendNotificationDto sendNotificationDto);

}
