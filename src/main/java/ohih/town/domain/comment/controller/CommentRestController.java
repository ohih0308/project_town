package ohih.town.domain.comment.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.SessionConst;
import ohih.town.constants.URLConst;
import ohih.town.domain.SimpleResponse;
import ohih.town.domain.comment.dto.CommentActionResult;
import ohih.town.domain.comment.dto.CommentContentInfo;
import ohih.town.domain.comment.service.CommentService;
import ohih.town.domain.common.dto.ActionResult;
import ohih.town.domain.common.dto.AuthorInfo;
import ohih.town.domain.common.service.CommonService;
import ohih.town.domain.user.dto.UserInfo;
import ohih.town.exception.InvalidAccessException;
import ohih.town.session.SessionManager;
import ohih.town.utilities.Utilities;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentRestController {

    private final CommonService commonService;
    private final CommentService commentService;


    @PostMapping(URLConst.UPLOAD_COMMENT)
    public ActionResult uploadComment(HttpServletRequest request,
                                      @Nullable @SessionAttribute(SessionConst.USER_INFO) UserInfo userInfo,
                                      AuthorInfo authorInfo, CommentContentInfo commentContentInfo) {
        ActionResult actionResult = new ActionResult();

        commonService.setAuthor(authorInfo, userInfo, Utilities.getIp(request));

        if (!commentService.checkValidations(actionResult, authorInfo, commentContentInfo.getComment())) {
            return actionResult;
        }

        commentService.uploadCommentExceptionHandler(actionResult, authorInfo, commentContentInfo);
        return actionResult;
    }

    @PostMapping(URLConst.CHECK_COMMENT_PERMISSION)
    public SimpleResponse checkCommentAccessPermission(HttpServletRequest request,
                                                       @Nullable @SessionAttribute(SessionConst.USER_INFO) UserInfo userInfo,
                                                       Long commentId, String password) {
        SimpleResponse simpleResponse = commentService.checkPostAccessPermission(userInfo, password, commentId);
        if (simpleResponse.getSuccess()) {
            SessionManager.setAttributes(request, SessionConst.ACCESS_PERMITTED_COMMENT_ID, commentId);
        }

        return simpleResponse;
    }

    @PostMapping(URLConst.DELETE_COMMENT)
    public ActionResult deleteComment(HttpServletRequest request, Long commentId) {
        ActionResult actionResult = new ActionResult();

        Long permittedCommentId = (Long) SessionManager.getAttributes(request, SessionConst.ACCESS_PERMITTED_COMMENT_ID);
        if (permittedCommentId == null || !permittedCommentId.equals(commentId)) {
            throw new InvalidAccessException();
        }

        commentService.deleteCommentExceptionHandler(actionResult, commentId);

        return actionResult;
    }
}
