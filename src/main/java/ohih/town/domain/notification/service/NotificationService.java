package ohih.town.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.domain.notification.dto.Notification;
import ohih.town.domain.notification.mapper.NotificationMapper;
import ohih.town.domain.post.dto.PostAccessInfo;
import org.springframework.stereotype.Service;

import static ohih.town.constants.NotificationConst.NEW_COMMENT;
import static ohih.town.constants.NotificationConst.NOTIFICATION_COMMENT_UPLOADED;
import static ohih.town.constants.NotificationMessageResourceBundle.NOTIFICATION_MESSAGES;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationMapper notificationMapper;


    private Long createNotification(Notification notification) {
        if (!notificationMapper.createNotification(notification)) {
            log.info("create notification failure. user_id = {}, notification type = {}",
                    notification.getUserId(), notification.getType());
        }

        return notification.getNotificationId();
    }


    public Long createPostCommentNotification(PostAccessInfo postAccessInfo) {
        Notification notification = new Notification();

        notification.setUserId(postAccessInfo.getUserId());
        notification.setType(NEW_COMMENT);
        notification.setMessage(NOTIFICATION_MESSAGES.getString(NOTIFICATION_COMMENT_UPLOADED));

        return createNotification(notification);
    }
}
