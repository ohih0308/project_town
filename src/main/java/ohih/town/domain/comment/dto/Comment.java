package ohih.town.domain.comment.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Comment {

    private Long postId;
    private Long commentId;

    private byte userType;
    private Long userId;
    private String userIp;
    private String author;

    private String savedFileName;
    private String extension;

    private String comment;
    private Date createdAt;
}
