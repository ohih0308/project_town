package ohih.town.domain.mail.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmailVerificationResult {
    private Boolean success;
    private String resultMessage;
    private List<String> errorMessages;
    private String verifiedEmail;
}
