package ohih.town.domain.mail;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerificationResult {
    private Boolean success;
    private String message;
    private String verifiedEmail;
}
