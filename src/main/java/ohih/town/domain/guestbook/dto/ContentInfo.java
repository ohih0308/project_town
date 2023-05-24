package ohih.town.domain.guestbook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentInfo {
    private Long postId;
    private Long ownerId;
    private boolean privateRead;
    private String content;
}
