package ohih.town.domain.post.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Appraisal {
    private Long appraisalId;
    private Long userId;
    private Long postId;
    private boolean isLike;
    private String ip;
}
