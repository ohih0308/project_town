package ohih.town.domain.post.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostUpdateInfo {
    private Long postId;
    private Long userId;
    private String author;
    private String subject;
    private String body;
}
