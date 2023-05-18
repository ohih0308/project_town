package ohih.town.domain.board.service;

import ohih.town.domain.board.dto.Board;
import ohih.town.domain.board.dto.BoardPost;
import ohih.town.utilities.Paging;
import ohih.town.utilities.Search;

import java.util.List;
import java.util.Map;

public interface BoardService {

    String getBoardName(Long boardId);

    List<Board> getBoards();

    Long countPosts(Long boardId, Search search);

    List<BoardPost> getPosts(Long boardId, Paging paging, Search search);
}
