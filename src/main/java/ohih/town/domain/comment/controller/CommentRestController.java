package ohih.town.domain.comment.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.SessionConst;
import ohih.town.constants.URLConst;
import ohih.town.domain.AccessPermissionCheckResult;
import ohih.town.domain.comment.dto.CommentContentInfo;
import ohih.town.domain.comment.dto.CommentDeleteResult;
import ohih.town.domain.comment.dto.CommentUploadRequest;
import ohih.town.domain.comment.dto.CommentUploadResult;
import ohih.town.domain.comment.service.CommentService;
import ohih.town.domain.common.dto.AuthorInfo;
import ohih.town.domain.notification.service.NotificationService;
import ohih.town.domain.user.dto.UserInfo;
import ohih.town.session.SessionManager;
import ohih.town.utilities.Utilities;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentRestController {

    private final CommentService commentService;
    private final NotificationService notificationService;

    @PostMapping(URLConst.UPLOAD_COMMENT)
    public CommentUploadResult uploadComment(HttpServletRequest request,
                                             @Nullable @SessionAttribute UserInfo userInfo,
                                             AuthorInfo authorInfo, CommentContentInfo commentContentInfo) {
        String ip = Utilities.getIp(request);
        Utilities.setAuthor(authorInfo, userInfo, ip);

        CommentUploadResult commentUploadResult =
                commentService.uploadComment(new CommentUploadRequest(authorInfo, commentContentInfo));

        if (commentUploadResult.isUploaded()) {
            notificationService.createNewCommentNotification(commentUploadResult.getPostId());
        }

        return commentUploadResult;
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
    public CommentDeleteResult deleteComment(HttpServletRequest request, Long commentId) {
        Long accessPermittedCommentId = (Long) SessionManager.getAttributes(request, SessionConst.ACCESS_PERMITTED_COMMENT_ID);

        return commentService.deleteComment(accessPermittedCommentId, commentId);
    }


}
