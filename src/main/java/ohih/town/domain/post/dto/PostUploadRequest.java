package ohih.town.domain.post.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ohih.town.AuthorInfo;

@Getter
@Setter
@ToString
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


    public PostUploadRequest(AuthorInfo authorInfo, PostContentInfo postContentInfo) {
        this.userId = authorInfo.getUserId();
        this.ip = authorInfo.getIp();
        this.userType = authorInfo.getUserType();
        this.author = authorInfo.getAuthor();
        this.password = authorInfo.getPassword();

        this.postId = postContentInfo.getPostId();
        this.boardId = postContentInfo.getBoardId();
        this.subject = postContentInfo.getSubject();
        this.body = postContentInfo.getBody();
    }
}
