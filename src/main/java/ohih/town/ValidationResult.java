package ohih.town;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ValidationResult {
    private Boolean isValid;
    private Map<String, Boolean> fieldValidation;
    private Map<String, String> message;
}
