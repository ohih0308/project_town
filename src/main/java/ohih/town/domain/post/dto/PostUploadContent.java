package ohih.town.domain.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUploadContent {

    private Long boardId;
    private String subject;
    private String body;
}
