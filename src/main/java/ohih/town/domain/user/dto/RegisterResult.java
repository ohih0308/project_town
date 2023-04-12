package ohih.town.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class RegisterResult {
    private boolean success;
    private Map<String, String> errorMessages;
    private String resultMessage;
    private String redirectUrl;
}
