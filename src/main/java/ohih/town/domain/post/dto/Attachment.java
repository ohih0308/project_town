package ohih.town.domain.post.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
public class Attachment {
    private String imageData;

    private String fileName;
    private String extension;
    private Long postId;
    private String directory;

    private Integer size;
}
