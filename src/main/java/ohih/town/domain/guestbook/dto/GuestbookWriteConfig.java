package ohih.town.domain.guestbook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestbookWriteConfig {
    private boolean activation;
    private boolean memberWrite;
    private boolean guestWrite;
}
