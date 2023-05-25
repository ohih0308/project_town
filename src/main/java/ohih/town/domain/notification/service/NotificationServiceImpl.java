package ohih.town.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.DomainConst;
import ohih.town.constants.NotificationConst;
import ohih.town.constants.URLConst;
import ohih.town.domain.guestbook.mapper.GuestbookMapper;
import ohih.town.domain.notification.dto.Notification;
import ohih.town.domain.notification.dto.NotificationResult;
import ohih.town.domain.notification.mapper.NotificationMapper;
import ohih.town.domain.post.mapper.PostMapper;
import ohih.town.exception.PartialDeleteException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static ohih.town.constants.ErrorsConst.*;
import static ohih.town.constants.NotificationConst.NOTIFICATION_COMMENT_UPLOADED;
import static ohih.town.constants.ResourceBundleConst.*;
import static ohih.town.constants.SuccessConst.NOTIFICATION_DELETE_SUCCESS;
import static ohih.town.constants.SuccessConst.NOTIFICATION_MARK_AS_READ_SUCCESS;

@RequiredArgsConstructor
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final PostMapper postMapper;
    private final GuestbookMapper guestbookMapper;


    @Override
    public void createNotification(boolean isGuestbook, Long postId) {
        Long userId;
        String redirectUrl;

        if (isGuestbook) {
            userId = guestbookMapper.getUserId(postId);
            redirectUrl = "";
        } else {
            userId = postMapper.getUserId(postId);
            redirectUrl = URLConst.POST_DETAILS.replace("{postId}", postId.toString());
        }

        Notification notification = Notification.builder().
                userId(userId).
                type(NotificationConst.NEW_COMMENT).
                message(NOTIFICATION_MESSAGES.getString(NOTIFICATION_COMMENT_UPLOADED)).
                redirectUrl(redirectUrl).
                build();

        try {
            if (!notificationMapper.createNotification(notification)) {
                throw new SQLException();
            }
        } catch (Exception e) {
            log.info("{}", e.getMessage());
        }
    }

    @Override
    public NotificationResult markAsRead(Long userId, Long notificationId) {
        NotificationResult notificationResult = new NotificationResult();

        Notification notification = notificationMapper.getNotification(notificationId);

        if (notification == null) {
            notificationResult.setResultMessage(NOTIFICATION_ERROR_MESSAGES.getString(NOTIFICATION_EXISTENCE_ERROR));
            return notificationResult;
        }

        try {
            Map<String, Long> map = new HashMap<>();
            map.put(DomainConst.USER_ID, userId);
            map.put(DomainConst.NOTIFICATION_ID, notificationId);

            if (!notificationMapper.markAsRead(map)) {
                throw new SQLException();
            }
            notificationResult.setSuccess(true);
            notificationResult.setResultMessage(SUCCESS_MESSAGES.getString(NOTIFICATION_MARK_AS_READ_SUCCESS));
            notificationResult.setRedirectUrl(notification.getRedirectUrl());
        } catch (SQLException e) {
            log.info("{}", e.getMessage());
            notificationResult.setResultMessage(NOTIFICATION_ERROR_MESSAGES.getString(NOTIFICATION_MARK_AS_READ_FAILURE));
        }

        return notificationResult;
    }


    @Override
    public NotificationResult deleteNotification(Long userId, Long notificationId) {
        NotificationResult notificationResult = new NotificationResult();
        notificationResult.setNotificationId(notificationId);

        Notification notification = notificationMapper.getNotification(notificationId);

        if (!Objects.equals(notification.getUserId(), userId)) {
            notificationResult.setResultMessage(NOTIFICATION_ERROR_MESSAGES.getString(NOTIFICATION_ACCESS_DENIED));
            return notificationResult;
        }

        try {
            if (!notificationMapper.deleteNotification(notificationId)) {
                throw new SQLException();
            }
            notificationResult.setSuccess(true);
            notificationResult.setResultMessage(SUCCESS_MESSAGES.getString(NOTIFICATION_DELETE_SUCCESS));
        } catch (Exception e) {
            log.info("{}", e.getMessage());
            notificationResult.setResultMessage(NOTIFICATION_ERROR_MESSAGES.getString(NOTIFICATION_DELETE_FAILURE));
        }

        return notificationResult;
    }

    @Override
    public NotificationResult deleteNotifications(Long userId) {
        NotificationResult notificationResult = new NotificationResult();
        Integer notificationCount = notificationMapper.countNotification(userId);

        try {
            if (Objects.equals(notificationCount, notificationMapper.deleteNotifications(userId))) {
                throw new PartialDeleteException();
            }
            notificationResult.setSuccess(true);
            notificationResult.setResultMessage(SUCCESS_MESSAGES.getString(NOTIFICATION_DELETE_SUCCESS));
        } catch (PartialDeleteException e) {
            log.info("{}", e.getMessage());
            notificationResult.setResultMessage(NOTIFICATION_ERROR_MESSAGES.getString(NOTIFICATION_DELETE_PARTIAL_FAILURE));
        } catch (Exception e) {
            log.info("{}", e.getMessage());
            notificationResult.setResultMessage(NOTIFICATION_ERROR_MESSAGES.getString(NOTIFICATION_DELETE_FAILURE));
        }

        return notificationResult;
    }
}
