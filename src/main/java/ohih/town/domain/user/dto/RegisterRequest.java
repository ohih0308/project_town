package ohih.town.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class RegisterRequest {

    private String email;
    private String username;
    private String password;
    private String confirmPassword;
    private Boolean agreement;
}
