package ohih.town.domain.forum.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BoardPost {

    private Long postId;
    private Long userId;
    private Integer userType;
    private String author;
    private String subject;
    private Long views;
    private Date createdAt;

    private String savedFileName;
    private String extension;
}
