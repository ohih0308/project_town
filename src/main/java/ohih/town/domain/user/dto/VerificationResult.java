package ohih.town.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class EmailVerificationResult {
    private Boolean isVerified;

    private Map<String, String> messages;
    private String verifiedValue;
}
