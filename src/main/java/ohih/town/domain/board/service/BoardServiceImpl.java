package ohih.town.domain.board.service;

import lombok.RequiredArgsConstructor;
import ohih.town.constants.*;
import ohih.town.domain.SimpleResponse;
import ohih.town.domain.VerificationResult;
import ohih.town.domain.board.dto.Board;
import ohih.town.domain.board.mapper.BoardMapper;
import ohih.town.utilities.Utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ohih.town.constants.DomainConst.NAME;
import static ohih.town.constants.ErrorConst.*;
import static ohih.town.constants.ResourceBundleConst.BOARD_ERROR_MESSAGES;
import static ohih.town.constants.ResourceBundleConst.SUCCESS_MESSAGES;
import static ohih.town.constants.SuccessConst.*;

@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardMapper boardMapper;


    @Override
    public boolean isBoardActivated(Long boardId) {
        return boardMapper.isBoardActivated(boardId);
    }

    @Override
    public String getBoardName(Long boardId) {
        return boardMapper.getBoardName(boardId);
    }

    @Override
    public List<Board> getBoards() {
        return boardMapper.getBoards();
    }

    @Override
    public VerificationResult verifyCategory(String name) {
        VerificationResult verificationResult = new VerificationResult();
        Map<String, String> messages = new HashMap<>();

        if (name == null) {
            messages.put(DomainConst.NAME, BOARD_ERROR_MESSAGES.getString(BOARD_NAME_INVALID));
        } else {
            boolean isValidated = Utilities.isValidated(ValidationPatterns.BOARD, name);
            boolean isDuplicated = isDuplicated(TableNameConst.CATEGORIES, DomainConst.NAME, name);

            if (!isValidated) {
                messages.put(DomainConst.NAME, BOARD_ERROR_MESSAGES.getString(CATEGORY_NAME_INVALID));
            }
            if (isDuplicated) {
                messages.put(DomainConst.NAME, BOARD_ERROR_MESSAGES.getString(CATEGORY_NAME_DUPLICATED));
            }

            if (isValidated && !isDuplicated) {
                verificationResult.setVerified(true);
                verificationResult.setVerifiedValue(name);
                messages.put(NAME, SUCCESS_MESSAGES.getString(CATEGORY_NAME_VALID));
            }
        }

        verificationResult.setMessages(messages);

        return verificationResult;
    }

    @Override
    public VerificationResult verifyBoard(Long categoryId, String name) {
        VerificationResult verificationResult = new VerificationResult();
        Map<String, String> messages = new HashMap<>();

        boolean existence = boardMapper.ifCategoryExists(categoryId);

        if (categoryId == null || !existence) {
            messages.put(DomainConst.CATEGORY_ID, BOARD_ERROR_MESSAGES.getString(CATEGORY_NOT_EXIST_ERROR));
        } else if (name == null) {
            messages.put(DomainConst.NAME, BOARD_ERROR_MESSAGES.getString(BOARD_NAME_NULL));
        } else {
            boolean isValidated = Utilities.isValidated(ValidationPatterns.BOARD, name);
            boolean isDuplicated = isDuplicated(TableNameConst.BOARDS, DomainConst.NAME, name);

            if (!isValidated) {
                messages.put(DomainConst.NAME, BOARD_ERROR_MESSAGES.getString(BOARD_NAME_INVALID));
            }
            if (isDuplicated) {
                messages.put(DomainConst.NAME, BOARD_ERROR_MESSAGES.getString(BOARD_NAME_DUPLICATED));
            }

            if (isValidated && !isDuplicated) {
                verificationResult.setVerified(true);
                verificationResult.setVerifiedValue(name);
                messages.put(NAME, SUCCESS_MESSAGES.getString(BOARD_NAME_VALID));
            }
        }

        verificationResult.setMessages(messages);

        return verificationResult;
    }

    @Override
    public boolean isDuplicated(String tableName, String field, String value) {
        Map<String, String> map = new HashMap<>();
        map.put(TableNameConst.TABLE_NAME, tableName);
        map.put(UtilityConst.FIELD, field);
        map.put((UtilityConst.VALUE), value);

        return boardMapper.isDuplicated(map);
    }


    @Override
    public SimpleResponse createCategory(String name) {
        SimpleResponse simpleResponse = new SimpleResponse();

        if (name == null) {
            simpleResponse.setMessage(BOARD_ERROR_MESSAGES.getString(BOARD_NAME_INVALID));
            return simpleResponse;
        }

        if (boardMapper.createCategory(name)) {
            simpleResponse.setSuccess(true);
            simpleResponse.setMessage(SUCCESS_MESSAGES.getString(CATEGORY_CREATE_SUCCESS));
        } else {
            simpleResponse.setMessage(BOARD_ERROR_MESSAGES.getString(CATEGORY_CREATE_FAILURE));
        }

        return simpleResponse;
    }

    @Override
    public SimpleResponse renameCategory(Long categoryId, String name) {
        SimpleResponse simpleResponse = new SimpleResponse();

        if (categoryId == null || name == null) {
            simpleResponse.setMessage(BOARD_ERROR_MESSAGES.getString(BOARD_NAME_INVALID));
            return simpleResponse;
        }

        Map<String, Object> map = new HashMap<>();
        map.put(DomainConst.CATEGORY_ID, categoryId);
        map.put(NAME, name);

        if (boardMapper.renameCategory(map)) {
            simpleResponse.setSuccess(true);
            simpleResponse.setMessage(SUCCESS_MESSAGES.getString(CATEGORY_RENAME_SUCCESS));
        } else {
            simpleResponse.setMessage(BOARD_ERROR_MESSAGES.getString(CATEGORY_RENAME_FAILURE));
        }

        return simpleResponse;
    }

    @Override
    public SimpleResponse createBoard(Long categoryId, String name) {
        SimpleResponse simpleResponse = new SimpleResponse();
        Map<String, Object> map = new HashMap<>();
        map.put(DomainConst.CATEGORY_ID, categoryId);
        map.put(NAME, name);

        if (boardMapper.createBoard(map)) {
            simpleResponse.setSuccess(true);
            simpleResponse.setMessage(SUCCESS_MESSAGES.getString(BOARD_CREATE_SUCCESS));
        } else {
            simpleResponse.setMessage(BOARD_ERROR_MESSAGES.getString(BOARD_CREATE_FAILURE));
        }
        return simpleResponse;
    }

    @Override
    public SimpleResponse renameBoard(Long boardId, String name) {
        SimpleResponse simpleResponse = new SimpleResponse();

        if (!Utilities.isValidated(ValidationPatterns.BOARD, name)) {
            simpleResponse.setMessage(BOARD_ERROR_MESSAGES.getString(BOARD_NAME_INVALID));
            return simpleResponse;
        }

        Map<String, Object> map = new HashMap<>();
        map.put(DomainConst.BOARD_ID, boardId);
        map.put(NAME, name);

        if (boardMapper.renameBoard(map)) {
            simpleResponse.setSuccess(true);
            simpleResponse.setMessage(SUCCESS_MESSAGES.getString(BOARD_RENAME_SUCCESS));
        } else {
            simpleResponse.setMessage(BOARD_ERROR_MESSAGES.getString(BOARD_RENAME_FAILURE));
        }

        return simpleResponse;
    }
}
