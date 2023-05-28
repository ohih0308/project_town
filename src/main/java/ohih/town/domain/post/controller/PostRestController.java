package ohih.town.domain.post.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.SessionConst;
import ohih.town.constants.URLConst;
import ohih.town.domain.AccessPermissionCheckResult;
import ohih.town.domain.common.dto.AuthorInfo;
import ohih.town.domain.post.dto.*;
import ohih.town.domain.post.service.PostServiceImpl;
import ohih.town.domain.user.dto.UserInfo;
import ohih.town.session.SessionManager;
import ohih.town.utilities.Utilities;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PostRestController {

    private final PostServiceImpl postService;


    @PostMapping(URLConst.UPLOAD_POST)
    public PostResult uploadPost(HttpServletRequest request,
                                 @Nullable @SessionAttribute UserInfo userInfo,
                                 AuthorInfo authorInfo, PostContentInfo postContentInfo) {
        String ip = Utilities.getIp(request);
        Utilities.setAuthor(authorInfo, userInfo, ip);

        List<Attachment> attachments = postService.extractAttachments(
                postContentInfo.getBoardId(),
                postContentInfo.getBody());

        postContentInfo.setBody(Utilities.replaceAttachments(postContentInfo.getBody(), attachments));

        return postService.uploadPost(new PostUploadRequest(authorInfo, postContentInfo), attachments);
    }

    @PostMapping(URLConst.ACCESS_PERMISSION_POST)
    public AccessPermissionCheckResult checkAccessPermission(HttpServletRequest request,
                                                             @Nullable @SessionAttribute UserInfo userInfo,
                                                             Long postId, String password) {
        AccessPermissionCheckResult accessPermissionCheckResult;

        if (userInfo == null) {
            accessPermissionCheckResult = postService.checkAccessPermission(null, postId, password);
        } else {
            accessPermissionCheckResult = postService.checkAccessPermission(userInfo.getUserId(), postId, password);
        }

        if (accessPermissionCheckResult.isAccessible()) {
            SessionManager.setAttributes(request, SessionConst.ACCESS_PERMITTED_POST_ID, postId);
        }

        return accessPermissionCheckResult;
    }

    @PostMapping(URLConst.UPDATE_POST)
    public PostResult updatePost(HttpServletRequest request,
                                 @Nullable @SessionAttribute UserInfo userInfo,
                                 AuthorInfo authorInfo, PostContentInfo postContentInfo) {
        String ip = Utilities.getIp(request);
        Utilities.setAuthor(authorInfo, userInfo, ip);

        List<Attachment> attachments = postService.extractAttachments(
                postContentInfo.getBoardId(),
                postContentInfo.getBody());

        postContentInfo.setBody(Utilities.replaceAttachments(postContentInfo.getBody(), attachments));

        Long accessPermittedPostId = (Long) SessionManager.getAttributes(request, SessionConst.ACCESS_PERMITTED_POST_ID);

        PostResult postResult = postService.updatePost(accessPermittedPostId, new PostUploadRequest(authorInfo, postContentInfo), attachments);

        if (postResult.isSuccess()) {
            SessionManager.removeAttribute(request, SessionConst.ACCESS_PERMITTED_POST_ID);
        }

        return postResult;
    }

    @PostMapping(URLConst.DELETE_POST)
    public PostResult deletePost(HttpServletRequest request,
                                 Long postId) {
        Long accessPermittedPostId = (Long) SessionManager.getAttributes(request, SessionConst.ACCESS_PERMITTED_POST_ID);

        PostResult postResult = postService.deletePost(accessPermittedPostId, postId);

        if (postResult.isSuccess()) {
            SessionManager.removeAttribute(request, SessionConst.ACCESS_PERMITTED_POST_ID);
        }

        return postResult;
    }


    @PostMapping(URLConst.APPRAISE_POST)
    public PostResult appraisePost(HttpServletRequest request,
                                   @Nullable @SessionAttribute UserInfo userInfo,
                                   Appraisal appraisal) {
        String ip = Utilities.getIp(request);

        if (postService.hasUserAppraised(userInfo == null ? null : userInfo.getUserId(), ip, appraisal.getPostId())) {
            return postService.uploadAppraisal(appraisal);
        } else {
            return postService.updateAppraisal(appraisal);
        }
    }
}
