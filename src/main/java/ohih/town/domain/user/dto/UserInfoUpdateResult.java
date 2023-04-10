package ohih.town.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserInfoUpdateResult {
    private boolean success;
    private List<String> messages;
}
