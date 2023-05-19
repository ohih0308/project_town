package ohih.town.domain.notification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationResult {

    private boolean isSuccess;
    private Long notificationId;
    private String resultMessage;

    private String redirectUrl;
}
