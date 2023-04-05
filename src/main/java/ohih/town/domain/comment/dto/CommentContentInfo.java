package ohih.town.domain.comment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentContentInfo {
    private Long postId;
    private String comment;
}
