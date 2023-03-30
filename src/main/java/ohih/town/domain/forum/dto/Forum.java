package ohih.town.domain.forum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Forum {
    private Long categoryId;
    private String categoryName;
    private Long boardId;
    private String boardName;
}
