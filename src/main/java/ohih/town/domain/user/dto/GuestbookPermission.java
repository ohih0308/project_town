package ohih.town.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestbookPermission {
    private Long userId;

    private boolean private_read;
    private boolean memberWrite;
    private boolean guestWrite;
}
