package ohih.town.domain.board.mapper;

import ohih.town.domain.board.dto.Board;
import ohih.town.domain.post.dto.SimplePost;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface BoardMapper {

    boolean ifCategoryExists(Long categoryId);

    boolean isBoardActivated(Long boardId);

    String getBoardName(Long boardId);

    List<Board> getBoards();

    Long countPosts(Map<String, Object> map);

    boolean isDuplicated(Map<String, String> map);

    boolean createCategory(String name);

    boolean renameCategory(Map<String, Object> map);

    boolean createBoard(Map<String, Object> map);

    boolean renameBoard(Map<String, Object> map);
}
