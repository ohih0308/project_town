package ohih.town.domain.post.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PostDetails {
    private Long postId;
    private Long boardId;
    private Integer userType;
    private Long userId;
    private String ip;
    private String author;
    private String subject;
    private String body;
    private Long views;
    private Date createdAt;
    private Date updatedAt;
    private String savedFileName;
    private String extension;
}
