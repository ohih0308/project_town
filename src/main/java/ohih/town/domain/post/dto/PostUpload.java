package ohih.town.domain.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUpload {
    private Long userId;
    private String ip;
    private Integer userType;
    private String author;
    private String password;

    private Long postId;
    private Long boardId;
    private String subject;
    private String body;


    public PostUpload(PostUploadUser postUploadUser, PostUploadContent postUploadContent) {
        this.userId = postUploadUser.getUserId();
        this.ip = postUploadUser.getIp();
        this.userType = postUploadUser.getUserType();
        this.author = postUploadUser.getAuthor();
        this.password = postUploadUser.getPassword();
        this.boardId = postUploadContent.getBoardId();
        this.subject = postUploadContent.getSubject();
        this.body = postUploadContent.getBody();
    }
}
