package ohih.town.domain.post.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PostEditResult {

    private boolean success;
    private Long postId;

    private String successMessage;

    private List<Map<String, Boolean>> fieldValidations;
    private List<Map<String, String>> errorMessages;

    private String redirectUrl;

    public PostEditResult() {
        this.success = false;
    }
}
