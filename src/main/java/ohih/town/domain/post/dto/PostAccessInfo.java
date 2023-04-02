package ohih.town.domain.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostAccessInfo {
    private Long postId;
    private Long userId;
    private Integer userType;
    private String password;
}
