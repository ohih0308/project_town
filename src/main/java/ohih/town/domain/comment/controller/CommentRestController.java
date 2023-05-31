package ohih.town.domain.comment.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.DomainConst;
import ohih.town.constants.SessionConst;
import ohih.town.constants.URLConst;
import ohih.town.constants.UtilityConst;
import ohih.town.domain.AccessPermissionCheckResult;
import ohih.town.domain.comment.dto.CommentContentInfo;
import ohih.town.domain.comment.dto.CommentUploadRequest;
import ohih.town.domain.comment.dto.CommentResult;
import ohih.town.domain.comment.service.CommentServiceImpl;
import ohih.town.AuthorInfo;
import ohih.town.domain.notification.service.NotificationServiceImpl;
import ohih.town.domain.user.dto.UserInfo;
import ohih.town.session.SessionManager;
import ohih.town.utilities.Paging;
import ohih.town.utilities.Utilities;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentRestController {

    private final CommentServiceImpl commentService;
    private final NotificationServiceImpl notificationService;


    @PostMapping(URLConst.UPLOAD_COMMENT)
    public CommentResult uploadComment(HttpServletRequest request,
                                       @Nullable @SessionAttribute UserInfo userInfo,
                                       AuthorInfo authorInfo, CommentContentInfo commentContentInfo) {
        String ip = Utilities.getIp(request);
        Utilities.setAuthor(authorInfo, userInfo, ip);

        CommentResult commentResult =
                commentService.uploadComment(new CommentUploadRequest(authorInfo, commentContentInfo));

        if (commentResult.isSuccess()) {
            notificationService.createNotification(false, commentResult.getPostId());
        }

        return commentResult;
    }

    @PostMapping(URLConst.ACCESS_PERMISSION_COMMENT)
    public AccessPermissionCheckResult checkAccessPermission(HttpServletRequest request,
                                                             @Nullable @SessionAttribute UserInfo userInfo,
                                                             Long commentId, String password) {
        AccessPermissionCheckResult accessPermissionCheckResult;

        if (userInfo == null) {
            accessPermissionCheckResult = commentService.checkAccessPermission(null, commentId, password);
        } else {
            accessPermissionCheckResult = commentService.checkAccessPermission(userInfo.getUserId(), commentId, password);
        }

        if (accessPermissionCheckResult.isAccessible()) {
            SessionManager.setAttributes(request, SessionConst.ACCESS_PERMITTED_COMMENT_ID, commentId);
        }

        return accessPermissionCheckResult;
    }

    @PostMapping(URLConst.DELETE_COMMENT)
    public CommentResult deleteComment(HttpServletRequest request, Long commentId) {
        Long accessPermittedCommentId = (Long) SessionManager.getAttributes(request, SessionConst.ACCESS_PERMITTED_COMMENT_ID);

        CommentResult commentResult = commentService.deleteComment(accessPermittedCommentId, commentId);

        if (commentResult.isSuccess()) {
            SessionManager.removeAttribute(request, SessionConst.ACCESS_PERMITTED_COMMENT_ID);
        }

        return commentResult;
    }

    @PostMapping(URLConst.GET_COMMENTS)
    public Map<String, Object> getComments(@PathVariable Long postId,
                                           Integer presentPage) {
        Map<String, Object> map = new HashMap<>();

        Long totalCount = commentService.countComments(postId);
        Paging paging = Utilities.getPaging(totalCount, presentPage, UtilityConst.COMMENTS_PER_PAGE);

        map.put(UtilityConst.PAGING, paging);
        map.put(DomainConst.COMMENTS, commentService.getComments(postId, paging));

        return map;
    }
}
