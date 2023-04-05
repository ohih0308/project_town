package ohih.town.domain.comment.dto;

import java.util.List;
import java.util.Map;

public class CommentActionResult {
    private Boolean success;

    private String successMessage;
    private List<Map<String, Boolean>> fieldValidations;
    private List<Map<String, String>> errorMessages;

    public CommentActionResult() {
        this.success = false;
    }
}
