package ohih.town.domain.comment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDeleteResult {
    private boolean isDeleted;
    private Long commentId;
    private String message;
}
