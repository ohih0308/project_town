package ohih.town.domain.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostContentInfo {

    private Long boardId;
    private String subject;
    private String body;
}
