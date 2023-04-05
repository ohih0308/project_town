package ohih.town.domain.notification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Notification {

    private Long notificationId;
    private Long userId;
    private Integer type;
    private String message;
}
