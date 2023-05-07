package ohih.town.domain.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserInfo {
    private Long userId;
    private Integer userType;
    private String email;
    private String username;

    private String directory;
}
