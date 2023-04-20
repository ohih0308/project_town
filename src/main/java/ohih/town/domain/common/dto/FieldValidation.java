package ohih.town.domain.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class FieldValidation {
    private boolean isValid;
    private boolean isDuplicated;

    private Map<String, String> messages;
}
