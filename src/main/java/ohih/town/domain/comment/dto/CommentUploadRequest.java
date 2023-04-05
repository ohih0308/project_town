package ohih.town.domain.comment.dto;

import lombok.Getter;
import lombok.Setter;
import ohih.town.domain.common.dto.AuthorInfo;

@Getter
@Setter
public class CommentUploadRequest {
    private Long userId;
    private String ip;
    private Integer userType;
    private String author;
    private String password;

    private Long postId;
    private String comment;

    public CommentUploadRequest(AuthorInfo authorInfo, CommentContentInfo commentContentInfo) {
        this.userId = authorInfo.getUserId();
        this.ip = authorInfo.getIp();
        this.userType = authorInfo.getUserType();
        this.author = authorInfo.getAuthor();
        this.password = authorInfo.getPassword();

        this.postId = commentContentInfo.getPostId();
        this.comment = commentContentInfo.getComment();
    }
}
