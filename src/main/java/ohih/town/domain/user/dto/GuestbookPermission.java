package ohih.town.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestbookPermission {
    private Long userId;

    private boolean privateRead;
    private boolean memberWrite;
    private boolean guestWrite;
}
