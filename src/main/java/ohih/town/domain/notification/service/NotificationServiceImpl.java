package ohih.town.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.NotificationConst;
import ohih.town.constants.URLConst;
import ohih.town.domain.notification.dto.Notification;
import ohih.town.domain.notification.mapper.NotificationMapper;
import ohih.town.domain.post.mapper.PostMapper;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

import static ohih.town.constants.NotificationConst.NOTIFICATION_COMMENT_UPLOADED;
import static ohih.town.constants.ResourceBundleConst.NOTIFICATION_MESSAGES;

@RequiredArgsConstructor
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final PostMapper postMapper;


    @Override
    public void createNewCommentNotification(Long postId) {
        Long userId = postMapper.getUserIdByPostId(postId);

        Notification notification = Notification.builder().
                userId(userId).
                type(NotificationConst.NEW_COMMENT).
                message(NOTIFICATION_MESSAGES.getString(NOTIFICATION_COMMENT_UPLOADED)).
                redirectUrl(URLConst.POST_DETAILS.replace("{postId}", postId.toString())).
                build();

        try {
            if (!notificationMapper.createNotification(notification)) {
                throw new SQLException();
            }
        } catch (Exception e) {
            log.info("{}", e.getMessage());
        }
    }
}
