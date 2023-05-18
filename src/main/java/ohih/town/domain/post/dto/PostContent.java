package ohih.town.domain.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostContent {
    private Long postId;
    private Long boardId;

    private Long userId;
    private Integer userType;
    private String author;

    private String subject;
    private String body;
}
