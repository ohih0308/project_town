package ohih.town.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class VerificationResult {
    private boolean isVerified;

    private Map<String, String> messages;
    private String verifiedValue;
}
