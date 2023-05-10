package ohih.town.domain.post.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.URLConst;
import ohih.town.domain.common.dto.AuthorInfo;
import ohih.town.domain.post.dto.PostContentInfo;
import ohih.town.domain.post.dto.PostUploadResult;
import ohih.town.domain.user.dto.UserInfo;
import ohih.town.utilities.Utilities;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PostRestController {
//
//
//
//    @PostMapping(URLConst.UPLOAD_POST)
//    public ActionResult uploadPost(HttpServletRequest request,
//                                   @Nullable @SessionAttribute(SessionConst.USER_INFO) UserInfo userInfo,
//                                   AuthorInfo authorInfo, PostContentInfo postContentInfo) {
//        ActionResult actionResult = new ActionResult();
//
//        List<Attachment> attachments = postService123.extractAttachmentsFromPost(postContentInfo.getBoardId(),
//                extractBase64DataFromString(postContentInfo.getBody()));
//        Utilities.setAuthor(authorInfo, userInfo, getIp(request));
//        postService123.setPostContent(postContentInfo, attachments);
//
//        if (!postService123.checkValidations(actionResult, authorInfo, postContentInfo)) {
//            return actionResult;
//        }
//
//        postService123.uploadPostExceptionHandler(actionResult, attachments, authorInfo, postContentInfo);
//
//        return actionResult;
//    }
//
//
//    @PostMapping(URLConst.CHECK_POST_PERMISSION)
//    public SimpleResponse checkPostAccessPermission(HttpServletRequest request,
//                                                    @Nullable @SessionAttribute(SessionConst.USER_INFO) UserInfo userInfo,
//                                                    Long postId, String password) {
//        SimpleResponse simpleResponse = postService123.checkPostAccessPermission(userInfo, password, postId);
//        if (simpleResponse.getSuccess()) {
//            SessionManager.setAttributes(request, SessionConst.ACCESS_PERMITTED_POST_ID, postId);
//        }
//        return simpleResponse;
//    }
//
//    @PostMapping(URLConst.UPDATE_POST)
//    public ActionResult updatePost(HttpServletRequest request,
//                                   @Nullable @SessionAttribute(SessionConst.USER_INFO) UserInfo userInfo,
//                                   AuthorInfo authorInfo, PostContentInfo postContentInfo)
//            throws InvalidAccessException {
//        ActionResult actionResult = new ActionResult();
//
//        Long permittedPostId = (Long) SessionManager.getAttributes(request, SessionConst.ACCESS_PERMITTED_POST_ID);
//        if (permittedPostId == null || !permittedPostId.equals(postContentInfo.getPostId())) {
//            throw new InvalidAccessException();
//        }
//
//        List<Attachment> attachments = postService123.extractAttachmentsFromPost(postContentInfo.getBoardId(),
//                extractBase64DataFromString(postContentInfo.getBody()));
//        Utilities.setAuthor(authorInfo, userInfo, getIp(request));
//        postService123.setPostContent(postContentInfo, attachments);
//
//        if (!postService123.checkValidations(actionResult, authorInfo, postContentInfo)) {
//            return actionResult;
//        }
//
//
//        // access validated post id session check
//        postService123.updatePostExceptionHandler(actionResult, attachments, authorInfo, postContentInfo);
//        SessionManager.removeAttribute(request, SessionConst.ACCESS_PERMITTED_POST_ID);
//
//        return actionResult;
//    }
//
//    @PostMapping(URLConst.DELETE_POST)
//    public ActionResult deletePost(HttpServletRequest request,
//                                   Long postId) {
//        ActionResult actionResult = new ActionResult();
//
//        Long permittedPostId = (Long) SessionManager.getAttributes(request, SessionConst.ACCESS_PERMITTED_POST_ID);
//        if (permittedPostId == null || !permittedPostId.equals(postId)) {
//            throw new InvalidAccessException();
//        }
//
//        postService123.deletePostExceptionHandler(actionResult, postId, forumService123.getBoardNameByPostId(postId));
//
//        return actionResult;
//    }

    @PostMapping(URLConst.UPLOAD_POST)
    public PostUploadResult uploadPost(HttpServletRequest request,
                                       @Nullable @SessionAttribute UserInfo userInfo,
                                       AuthorInfo authorInfo, PostContentInfo postContentInfo) {
        String ip = Utilities.getIp(request);
        Utilities.setAuthor(authorInfo, userInfo, ip);


        return null;
    }
}
