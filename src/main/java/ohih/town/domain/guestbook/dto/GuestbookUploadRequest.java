package ohih.town.domain.guestbook.dto;

import lombok.*;
import ohih.town.AuthorInfo;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuestbookUploadRequest {

    private Long userId;
    private String ip;
    private Integer userType;
    private String author;
    private String password;

    private Long ownerId;
    private boolean privateRead;
    private Long contentId;
    private String content;


    public GuestbookUploadRequest(AuthorInfo authorInfo, ContentInfo contentInfo) {
        this.userId = authorInfo.getUserId();
        this.ip = authorInfo.getIp();
        this.userType = authorInfo.getUserType();
        this.author = authorInfo.getAuthor();
        this.password = authorInfo.getPassword();

        this.privateRead = contentInfo.isPrivateRead();
        this.ownerId = contentInfo.getOwnerId();
        this.content = contentInfo.getContent();
    }
}
