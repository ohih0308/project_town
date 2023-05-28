package ohih.town.domain.board.service;

import ohih.town.domain.SimpleResponse;
import ohih.town.domain.VerificationResult;
import ohih.town.domain.board.dto.Board;

import java.util.List;

public interface BoardService {

    boolean isBoardActivated(Long boardId);

    String getBoardName(Long boardId);

    List<Board> getBoards();

    VerificationResult verifyCategory(String name);

    VerificationResult verifyBoard(Long categoryId, String name);

    boolean isDuplicated(String tableName, String field, String value);


    SimpleResponse createCategory(String name);

    SimpleResponse renameCategory(Long categoryId, String name);

    SimpleResponse createBoard(Long categoryId, String name);

    SimpleResponse renameBoard(Long boardId, String name);
}
