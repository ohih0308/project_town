package ohih.town.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileImageActionResult {
    private boolean success;
    private String message;
    private ProfileImage profileImage;
}
