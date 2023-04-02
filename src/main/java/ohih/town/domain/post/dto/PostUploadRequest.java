package ohih.town.domain.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUploadRequest {
    private Long userId;
    private String ip;
    private Integer userType;
    private String author;
    private String password;

    private Long postId;
    private Long boardId;
    private String subject;
    private String body;


    public PostUploadRequest(PostAuthorInfo postAuthorInfo, PostContentInfo postContentInfo) {
        this.userId = postAuthorInfo.getUserId();
        this.ip = postAuthorInfo.getIp();
        this.userType = postAuthorInfo.getUserType();
        this.author = postAuthorInfo.getAuthor();
        this.password = postAuthorInfo.getPassword();
        this.boardId = postContentInfo.getBoardId();
        this.subject = postContentInfo.getSubject();
        this.body = postContentInfo.getBody();
    }
}
