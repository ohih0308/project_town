package ohih.town.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessInfo {
    private Long id;
    private Long userId;
    private Integer userType;
    private String password;
}
