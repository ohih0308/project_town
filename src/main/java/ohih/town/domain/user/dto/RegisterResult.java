package ohih.town.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class RegisterResult {

    private Boolean success;
    private Long userId;
    private List<Map<String, String>> errorFields;
    // errorFiled, errorValue
    private List<Map<String, String>> errorMessages;

    private String successMessage;

    private String redirectUrl;
}
