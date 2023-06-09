package ohih.town;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorInfo {
    private Long userId;
    private String ip;
    private Integer userType;
    private String author;
    private String password;
}