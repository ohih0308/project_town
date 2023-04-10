package ohih.town.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CheckResult {

    private boolean isValid;
    private boolean isDuplicated;

    private List<String> messages;
}
