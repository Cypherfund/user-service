package com.cypherfund.campaign.user.proxies;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "notification-service", url="${app.notification.endpoint}")
public interface NotificationFeignClient {
//    @PostMapping("/notif-api/send/push")
//    void pushClientNotification(@RequestBody SendNotificationDto sendNofication,
//                                @RequestParam("deviceToken") String deviceToken);
//    @PostMapping("/notif-api/send/sms")
//    void sendSmsNotification(@RequestBody SendNotificationDto sendNotificationDto);
//
//    @PostMapping("/notif-api/send/email")
//    void sendEmailNotification(@RequestBody SendNotificationDto sendNotificationDto);
}
