package ohih.town.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileImageResult {
    private boolean success;
    private String resultMessage;
    private String profileImageDirectory;
}
