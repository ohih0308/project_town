package ohih.town.domain.notification.service;

import ohih.town.domain.notification.dto.NotificationResult;

public interface NotificationService {

    void createNewCommentNotification(Long postId);

    NotificationResult markAsRead(Long userId, Long notificationId);

    NotificationResult deleteNotification(Long userId, Long notificationId);

    NotificationResult deleteNotifications(Long userId);
}
