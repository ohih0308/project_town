package ohih.town.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResult {

    private boolean success;
    private String message;
    private String redirectUrl;

    private UserInfo userInfo;
}
