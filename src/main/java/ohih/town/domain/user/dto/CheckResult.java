package ohih.town.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckResult {

    private Boolean isValid;
    private Boolean isDuplicated;

    private String message;
}
