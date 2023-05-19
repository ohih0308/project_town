package ohih.town.domain.board.service;

import lombok.RequiredArgsConstructor;
import ohih.town.domain.board.dto.Board;
import ohih.town.domain.board.mapper.BoardMapper;

import java.util.List;

@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardMapper boardMapper;

    @Override
    public String getBoardName(Long boardId) {
        return boardMapper.getBoardName(boardId);
    }

    @Override
    public List<Board> getBoards() {
        return boardMapper.getBoards();
    }
}
