package ohih.town.domain.post.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Attachment {
    private String imageDate;

    private String fileName;
    private String extension;
    private Long postId;
    private String directory;
}
