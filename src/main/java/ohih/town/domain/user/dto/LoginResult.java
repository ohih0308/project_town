package ohih.town.domain.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class LoginResult {

    private boolean success;
    private List<String> message;
    private String redirectUrl;

    private UserInfo userInfo;
}
