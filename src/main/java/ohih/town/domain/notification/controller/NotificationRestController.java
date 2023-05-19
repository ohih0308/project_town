package ohih.town.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.URLConst;
import ohih.town.domain.notification.dto.NotificationResult;
import ohih.town.domain.notification.service.NotificationServiceImpl;
import ohih.town.domain.user.dto.UserInfo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@Slf4j
@RequiredArgsConstructor
public class NotificationRestController {

    private final NotificationServiceImpl notificationService;


    @PostMapping(URLConst.READ_NOTIFICATIONS)
    public NotificationResult readNotifications(@SessionAttribute UserInfo userInfo,
                                    Long notificationId) {
        return notificationService.markAsRead(userInfo.getUserId(), notificationId);
    }

    @PostMapping(URLConst.DELETE_NOTIFICATION)
    public NotificationResult deleteNotification(@SessionAttribute UserInfo userInfo, Long notificationId) {
        return notificationService.deleteNotification(userInfo.getUserId(), notificationId);
    }

    @PostMapping(URLConst.DELETE_NOTIFICATIONS)
    public NotificationResult deleteNotifications(@SessionAttribute UserInfo userInfo) {
        return notificationService.deleteNotifications(userInfo.getUserId());
    }
}
