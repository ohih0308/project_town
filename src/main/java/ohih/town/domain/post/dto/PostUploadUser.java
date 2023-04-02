package ohih.town.domain.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUploadUser {
    private Long userId;
    private String ip;
    private Integer userType;
    private String author;
    private String password;
}