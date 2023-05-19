package ohih.town.domain.board.mapper;

import ohih.town.domain.board.dto.Board;
import ohih.town.domain.post.dto.SimplePost;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface BoardMapper {

    String getBoardName(Long boardId);

    List<Board> getBoards();

    Long countPosts(Map<String, Object> map);

    List<SimplePost> getPosts(Map<String, Object> map);
}
