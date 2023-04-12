package ohih.town.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class UserInfoUpdateResult {
    private boolean success;
    private Map<String, String> messages;
}
