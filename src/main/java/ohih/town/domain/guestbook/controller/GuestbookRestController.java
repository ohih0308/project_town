package ohih.town.domain.guestbook.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.SessionConst;
import ohih.town.constants.URLConst;
import ohih.town.domain.AccessPermissionCheckResult;
import ohih.town.domain.common.dto.AuthorInfo;
import ohih.town.domain.guestbook.dto.ContentInfo;
import ohih.town.domain.guestbook.dto.PostUploadRequest;
import ohih.town.domain.guestbook.service.GuestbookServiceImpl;
import ohih.town.domain.post.dto.PostResult;
import ohih.town.domain.user.dto.UserInfo;
import ohih.town.session.SessionManager;
import ohih.town.utilities.Utilities;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RequiredArgsConstructor
@Slf4j
@RestController
public class GuestbookRestController {

    private final GuestbookServiceImpl guestbookService;

    @PostMapping(URLConst.GUESTBOOK_UPLOAD_POST)
    public PostResult uploadPost(HttpServletRequest request,
                                 @Nullable @SessionAttribute UserInfo userInfo,
                                 AuthorInfo authorInfo, ContentInfo contentInfo) {
        String ip = Utilities.getIp(request);
        Utilities.setAuthor(authorInfo, userInfo, ip);

        return guestbookService.uploadPost(authorInfo, contentInfo);
    }

    @PostMapping(URLConst.GUESTBOOK_ACCESS_PERMISSION_POST)
    public AccessPermissionCheckResult checkAccessPermission(HttpServletRequest request,
                                                             @Nullable @SessionAttribute UserInfo userInfo,
                                                             Long postId, String password) {
        AccessPermissionCheckResult accessPermissionCheckResult;

        if (userInfo == null) {
            accessPermissionCheckResult = guestbookService.checkAccessPermission(null, postId, password);
        } else {
            accessPermissionCheckResult = guestbookService.checkAccessPermission(userInfo.getUserId(), postId, password);
        }

        if (accessPermissionCheckResult.isAccessible()) {
            SessionManager.setAttributes(request, SessionConst.ACCESS_PERMITTED_POST_ID, postId);
        }

        return accessPermissionCheckResult;
    }

    @PostMapping(URLConst.GUESTBOOK_DELETE_POST)
    public PostResult deletePost(HttpServletRequest request,
                                 Long postId) {
        Long accessPermittedPostId = (Long) SessionManager.getAttributes(request, SessionConst.ACCESS_PERMITTED_POST_ID);

        PostResult postResult = guestbookService.deletePost(accessPermittedPostId, postId);

        if (postResult.isSuccess()) {
            SessionManager.removeAttribute(request, SessionConst.ACCESS_PERMITTED_POST_ID);
        }

        return postResult;
    }
}
