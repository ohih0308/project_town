package ohih.town.domain.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CheckResult {

    private Boolean isValid;
    private Boolean isDuplicated;

    private String message;
}
