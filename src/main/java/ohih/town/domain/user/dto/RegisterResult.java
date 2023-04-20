package ohih.town.domain.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class RegisterResult {
    private boolean success;
    private Map<String, String> errorMessages = new HashMap<>();
    private String resultMessage;
    private String redirectUrl;
}
