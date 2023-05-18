package ohih.town.domain.comment.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CommentsList {

    private Long rowNum;
    private Long postId;
    private Long commentId;

    private byte userType;
    private Long userId;
    private String userIp;
    private String author;

    private String savedFileName;
    private String ext;

    private String comment;
    private Date createdAt;
}
