package ohih.town.domain.forum.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BoardPost {

    private Long id;
    private Long boardId;
    private Long userId;
    private Integer userType;
    private String ip;
    private String author;
    private String subject;
    private Long views;
    private Date createdAt;

    private String savedFileName;

    private String ext;
}
