package ohih.town.domain.board.service;

import ohih.town.domain.board.dto.Board;

import java.util.List;

public interface BoardService {

    String getBoardName(Long boardId);

    List<Board> getBoards();
}
