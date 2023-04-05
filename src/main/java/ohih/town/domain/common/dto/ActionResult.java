package ohih.town.domain.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ActionResult {

    private Boolean success;
    private Long postId;

    private String successMessage;

    private List<Map<String, Boolean>> fieldValidations;
    private List<Map<String, String>> errorMessages;

    private String redirectUrl;

    public ActionResult() {
        this.success = false;
    }
}
