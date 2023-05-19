package ohih.town.domain.notification.mapper;

import ohih.town.domain.notification.dto.Notification;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface NotificationMapper {

    Notification getNotification(Long notificationId);

    Integer countNotification(Long userId);


    boolean createNotification(Notification notification);

    boolean markAsRead(Map<String, Long> map);


    boolean deleteNotification(Long notificationId);

    Integer deleteNotifications(Long userId);

}
