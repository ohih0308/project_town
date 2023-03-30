package ohih.town.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfo {

    private Long id;
    private Integer userType;
    private String email;
    private String username;
    private String uuid;
    private String extension;
    private String directory;
}
