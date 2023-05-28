package ohih.town.domain.board.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.SessionConst;
import ohih.town.constants.URLConst;
import ohih.town.domain.SimpleResponse;
import ohih.town.domain.VerificationResult;
import ohih.town.domain.board.service.BoardServiceImpl;
import ohih.town.domain.user.dto.UserInfo;
import ohih.town.session.SessionManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import static ohih.town.constants.SessionConst.VERIFIED_BOARD_NAME;
import static ohih.town.constants.SessionConst.VERIFIED_CATEGORY_ID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class BoardRestController {

    private final BoardServiceImpl boardService;


    @PostMapping(URLConst.VERIFY_CATEGORY)
    public VerificationResult verifyCategory(HttpServletRequest request,
                                             String name) {
        VerificationResult verificationResult = boardService.verifyCategory(name);

        if (verificationResult.isVerified()) {
            SessionManager.setAttributes(request, SessionConst.VERIFIED_CATEGORY_NAME, name);
        }
        return verificationResult;
    }

    @PostMapping(URLConst.VERIFY_BOARD)
    public VerificationResult verifyBoard(HttpServletRequest request,
                                          Long categoryId, String name) {
        VerificationResult verificationResult = boardService.verifyBoard(categoryId, name);

        if (verificationResult.isVerified()) {
            SessionManager.setAttributes(request, VERIFIED_CATEGORY_ID, categoryId);
            SessionManager.setAttributes(request, VERIFIED_BOARD_NAME, name);
        }
        return verificationResult;
    }


    @PostMapping(URLConst.CREATE_CATEGORY)
    public SimpleResponse createCategory(HttpServletRequest request,
                                         @SessionAttribute UserInfo userInfo) {
        log.info("createCategory is called by {}", userInfo.toString());

        String name = (String) SessionManager.getAttributes(request, SessionConst.VERIFIED_CATEGORY_NAME);
        SessionManager.removeAttribute(request, SessionConst.VERIFIED_CATEGORY_NAME);

        return boardService.createCategory(name);
    }

    @PostMapping(URLConst.RENAME_CATEGORY)
    public SimpleResponse renameCategory(HttpServletRequest request,
                                         @SessionAttribute UserInfo userInfo,
                                         Long categoryId) {
        log.info("renameCategory is called by {}", userInfo.toString());

        String name = (String) SessionManager.getAttributes(request, SessionConst.VERIFIED_CATEGORY_NAME);
        SessionManager.removeAttribute(request, SessionConst.VERIFIED_CATEGORY_NAME);

        return boardService.renameCategory(categoryId, name);
    }

    @PostMapping(URLConst.CREATE_BOARD)
    public SimpleResponse createBoard(HttpServletRequest request,
                                      @SessionAttribute UserInfo userInfo) {
        log.info("createBoard is called by {}", userInfo.toString());

        Long categoryId = (Long) SessionManager.getAttributes(request, VERIFIED_CATEGORY_ID);
        String name = (String) SessionManager.getAttributes(request, VERIFIED_BOARD_NAME);

        SimpleResponse simpleResponse = boardService.createBoard(categoryId, name);

        SessionManager.removeAttribute(request, SessionConst.VERIFIED_CATEGORY_ID);
        SessionManager.removeAttribute(request, SessionConst.VERIFIED_BOARD_NAME);

        return simpleResponse;
    }

    @PostMapping(URLConst.RENAME_BOARD)
    public SimpleResponse renameBoard(@SessionAttribute UserInfo userInfo,
                            Long boardId, String name) {
        log.info("renameBoard is called by {}", userInfo.toString());

        return boardService.renameBoard(boardId, name);
    }
}
