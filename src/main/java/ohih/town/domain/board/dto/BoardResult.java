package ohih.town.domain.board.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardResult {
    private boolean isSuccess;
    private String resultMessage;
    private String category;
    private String board;
}
