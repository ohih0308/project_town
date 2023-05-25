package ohih.town.domain.guestbook.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Guestbook {

    private Long contentId;
    private Long userId;
    private Integer userType;
    private String ip;
    private String author;
    private Boolean privateRead;
    private String content;
    private Date createdAt;
    private String savedFileName;
    private String extension;
    private Integer commentCount;
}
