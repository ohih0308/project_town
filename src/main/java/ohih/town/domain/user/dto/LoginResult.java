package ohih.town.domain.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ohih.town.constants.URLConst;

import java.util.Map;

@Getter
@Setter
@ToString
public class LoginResult {

    private boolean success;
    private String resultMessage;
    private Map<String, String> errorMessages;
    private String redirectUrl = URLConst.LOGIN;

    private UserInfo userInfo;
}
