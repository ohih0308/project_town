package ohih.town.domain.comment.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class CommentResult {
    boolean isSuccess;
    private Map<String, String> errorMessages = new HashMap<>();
    private String resultMessage;

    private Long postId;
    private Long commentId;
}
