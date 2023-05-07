package ohih.town.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Notification {
    private Long notificationId;
    private Long userId;
    private Integer type;
    private String message;
    private String redirectUrl;
}
