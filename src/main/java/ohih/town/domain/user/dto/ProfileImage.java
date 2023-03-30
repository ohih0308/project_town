package ohih.town.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileImage {
    private String uuid;
    private Long userId;
    private String fileName;
    private String extension;
    private String directory;
}
