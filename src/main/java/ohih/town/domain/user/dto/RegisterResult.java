package ohih.town.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class RegisterResult {
    private boolean isRegistered;
    private Map<String, String> errorMessages = new HashMap<>();
    private String resultMessage;
    private String redirectUrl;
}
