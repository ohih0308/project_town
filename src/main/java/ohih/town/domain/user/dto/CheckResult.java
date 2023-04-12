package ohih.town.domain.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class CheckResult {

    private boolean isValid;
    private boolean isDuplicated;

    private Map<String, String> messages;
}
