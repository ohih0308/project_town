package ohih.town.domain.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class RegisterRequestResult {

    private Boolean success;
    private Long userId;
    private List<Map<String, String>> errorFields;
    // errorFiled, errorValue
    private List<Map<String, String>> errorMessages;

    private String successMessage;

    private String redirectUrl;

    public RegisterRequestResult() {
        this.success = false;
    }
}
