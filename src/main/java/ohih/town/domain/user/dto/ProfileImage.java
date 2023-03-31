package ohih.town.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileImage {
    private String savedFileName;
    private Long userId;
    private String originalFileName;
    private String extension;
    private String directory;
}
