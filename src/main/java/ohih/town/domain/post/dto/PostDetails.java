package ohih.town.domain.post.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
public class PostDetails {
    private Long id;
    private Long boardId;
    private Integer userType;
    private Long userId;
    private String ip;
    private String author;
    private String subject;
    private String body;
    private Long views;
    private Date createAt;
    private Date updatedAt;

    private Map<String, String> attachments;
}
