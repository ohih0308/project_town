package ohih.town.domain.post.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class PostUploadResult {
    boolean isUploaded;
    private Map<String, String> errorMessages = new HashMap<>();
    private String resultMessage;

    private Long postId;
}
