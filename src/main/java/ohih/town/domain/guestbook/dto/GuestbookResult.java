package ohih.town.domain.guestbook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestbookResult {

    private Long ownerId;
    private boolean isSuccess;
    private String message;
}
