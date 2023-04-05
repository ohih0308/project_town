package ohih.town.domain.notification.mapper;

import ohih.town.domain.notification.dto.Notification;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NotificationMapper {

    boolean createNotification(Notification notification);
}
