package ohih.town.domain.guestbook.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.DomainConst;
import ohih.town.constants.SessionConst;
import ohih.town.constants.URLConst;
import ohih.town.constants.UtilityConst;
import ohih.town.domain.AccessPermissionCheckResult;
import ohih.town.domain.comment.dto.CommentResult;
import ohih.town.AuthorInfo;
import ohih.town.domain.guestbook.dto.ContentInfo;
import ohih.town.domain.guestbook.dto.Guestbook;
import ohih.town.domain.guestbook.service.GuestbookServiceImpl;
import ohih.town.domain.notification.service.NotificationServiceImpl;
import ohih.town.domain.post.dto.PostResult;
import ohih.town.domain.user.dto.UserInfo;
import ohih.town.session.SessionManager;
import ohih.town.utilities.Paging;
import ohih.town.utilities.Utilities;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@RestController
public class GuestbookRestController {

    private final GuestbookServiceImpl guestbookService;
    private final NotificationServiceImpl notificationService;


    @PostMapping(URLConst.GUESTBOOK_UPLOAD_POST)
    public PostResult uploadPost(HttpServletRequest request,
                                 @Nullable @SessionAttribute UserInfo userInfo,
                                 AuthorInfo authorInfo, ContentInfo contentInfo) {
        String ip = Utilities.getIp(request);
        Utilities.setAuthor(authorInfo, userInfo, ip);

        PostResult postResult = guestbookService.uploadPost(authorInfo, contentInfo);

        if (postResult.isSuccess() &&
                userInfo != null &&
                Objects.equals(authorInfo.getUserId(), userInfo.getUserId())) {
            notificationService.createNotification(true, postResult.getPostId());
        }

        return postResult;
    }

    @PostMapping(URLConst.GUESTBOOK_ACCESS_PERMISSION_POST)
    public AccessPermissionCheckResult checkPostAccessPermission(HttpServletRequest request,
                                                                 @Nullable @SessionAttribute UserInfo userInfo,
                                                                 Long postId, String password) {
        AccessPermissionCheckResult accessPermissionCheckResult;

        if (userInfo == null) {
            accessPermissionCheckResult = guestbookService.checkPostAccessPermission(null, postId, password);
        } else {
            accessPermissionCheckResult = guestbookService.checkPostAccessPermission(userInfo.getUserId(), postId, password);
        }

        if (accessPermissionCheckResult.isAccessible()) {
            SessionManager.setAttributes(request, SessionConst.ACCESS_PERMITTED_GUESTBOOK_POST_ID, postId);
        }

        return accessPermissionCheckResult;
    }

    @PostMapping(URLConst.GUESTBOOK_DELETE_POST)
    public PostResult deletePost(HttpServletRequest request,
                                 Long postId) {
        Long accessPermittedPostId = (Long) SessionManager.getAttributes(request, SessionConst.ACCESS_PERMITTED_GUESTBOOK_POST_ID);

        PostResult postResult = guestbookService.deletePost(accessPermittedPostId, postId);

        if (postResult.isSuccess()) {
            SessionManager.removeAttribute(request, SessionConst.ACCESS_PERMITTED_GUESTBOOK_POST_ID);
        }

        return postResult;
    }


    @PostMapping(URLConst.GUESTBOOK_UPLOAD_COMMENT)
    public CommentResult uploadComment(HttpServletRequest request,
                                       @Nullable @SessionAttribute UserInfo userInfo,
                                       AuthorInfo authorInfo, ContentInfo contentInfo) {
        String ip = Utilities.getIp(request);
        Utilities.setAuthor(authorInfo, userInfo, ip);

        CommentResult commentResult = guestbookService.uploadComment(authorInfo, contentInfo);

        if (commentResult.isSuccess()) {
            notificationService.createNotification(true, commentResult.getPostId());
        }

        return commentResult;
    }

    @PostMapping(URLConst.GUESTBOOK_ACCESS_PERMISSION_COMMENT)
    public AccessPermissionCheckResult checkCommentAccessPermission(HttpServletRequest request,
                                                                    @Nullable @SessionAttribute UserInfo userInfo,
                                                                    Long commentId, String password) {
        AccessPermissionCheckResult accessPermissionCheckResult;

        if (userInfo == null) {
            accessPermissionCheckResult = guestbookService.checkCommentAccessPermission(null, commentId, password);
        } else {
            accessPermissionCheckResult = guestbookService.checkCommentAccessPermission(userInfo.getUserId(), commentId, password);
        }

        if (accessPermissionCheckResult.isAccessible()) {
            SessionManager.setAttributes(request, SessionConst.ACCESS_PERMITTED_GUESTBOOK_COMMENT_ID, commentId);
        }

        return accessPermissionCheckResult;
    }

    @PostMapping(URLConst.GUESTBOOK_DELETE_COMMENT)
    public CommentResult deleteComment(HttpServletRequest request,
                                       Long commentId) {
        Long accessPermittedCommentId = (Long) SessionManager.getAttributes(request, SessionConst.ACCESS_PERMITTED_GUESTBOOK_COMMENT_ID);

        CommentResult commentResult = guestbookService.deleteComment(accessPermittedCommentId, commentId);

        if (commentResult.isSuccess()) {
            SessionManager.removeAttribute(request, SessionConst.ACCESS_PERMITTED_COMMENT_ID);
        }

        return commentResult;
    }

    @PostMapping(URLConst.GUESTBOOK_COMMENTS)
    public Map<String, Object> getGuestbookComments(Long postId,
                                                    @SessionAttribute UserInfo userInfo,
                                                    Integer presentPage) {
        Map<String, Object> map = new HashMap<>();

        Long totalCount = guestbookService.countComments(postId);
        Paging paging = Utilities.getPaging(totalCount, presentPage, UtilityConst.COMMENTS_PER_PAGE);
        List<Guestbook> comments = guestbookService.getComments(postId, userInfo.getUserId(), paging);

        map.put(UtilityConst.PAGING, paging);
        map.put(DomainConst.COMMENTS, comments);

        return map;
    }
}
