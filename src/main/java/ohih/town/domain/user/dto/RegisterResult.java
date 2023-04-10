package ohih.town.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RegisterResult {
    private boolean success;
    private List<String> messages;
    private String redirectUrl;
}
