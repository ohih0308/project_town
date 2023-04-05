package ohih.town.domain.comment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentAccessInfo {
    private Long commentId;
    private Long userId;
    private Integer userType;
    private String password;
}
