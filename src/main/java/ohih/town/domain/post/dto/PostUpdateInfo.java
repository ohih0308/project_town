package ohih.town.domain.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUpdateInfo {
    private Long postId;
    private Long userId;
    private String author;
    private String subject;
    private String body;
}
