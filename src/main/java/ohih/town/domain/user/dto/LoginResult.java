package ohih.town.domain.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ohih.town.constants.URLConst;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class LoginResult {

    private boolean isLoggedIn;
    private String resultMessage;
    private Map<String, String> errorMessages = new HashMap<>();
    private String redirectUrl = URLConst.LOGIN;

    private UserInfo userInfo;
}
