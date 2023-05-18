package ohih.town.domain.board.service;

import lombok.RequiredArgsConstructor;
import ohih.town.domain.board.dto.Board;
import ohih.town.domain.board.dto.BoardPost;
import ohih.town.domain.board.mapper.BoardMapper;
import ohih.town.utilities.Paging;
import ohih.town.utilities.Search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ohih.town.constants.DomainConst.BOARD_ID;
import static ohih.town.constants.UtilityConst.PAGING;
import static ohih.town.constants.UtilityConst.SEARCH;

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

    @Override
    public Long countPosts(Long boardId, Search search) {
        Map<String, Object> map = new HashMap<>();
        map.put(BOARD_ID, boardId);
        map.put(SEARCH, search);
        return boardMapper.countPosts(map);
    }

    @Override
    public List<BoardPost> getPosts(Long boardId, Paging paging, Search search) {
        Map<String, Object> map = new HashMap<>();
        map.put(BOARD_ID, boardId);
        map.put(PAGING, paging);
        map.put(SEARCH, search);
        return boardMapper.getPosts(map);
    }
}
