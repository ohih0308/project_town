package ohih.town.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessPermissionCheckResult {
    private boolean isAccessible;
    private Long id;
    private String message;
}
