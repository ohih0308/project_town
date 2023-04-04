package ohih.town.domain.post.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.SessionConst;
import ohih.town.constants.URLConst;
import ohih.town.domain.SimpleResponse;
import ohih.town.domain.post.dto.Attachment;
import ohih.town.domain.post.dto.PostAuthorInfo;
import ohih.town.domain.post.dto.PostContentInfo;
import ohih.town.domain.post.dto.PostEditResult;
import ohih.town.domain.post.service.PostService;
import ohih.town.domain.user.dto.UserInfo;
import ohih.town.exception.InvalidAccessException;
import ohih.town.session.SessionManager;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

import static ohih.town.utilities.Utilities.extractBase64DataFromString;
import static ohih.town.utilities.Utilities.getIp;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PostRestController {

    private final PostService postService;


    @PostMapping(URLConst.UPLOAD_POST)
    public PostEditResult uploadPost(HttpServletRequest request,
                                     @Nullable @SessionAttribute(SessionConst.USER_INFO) UserInfo userInfo,
                                     PostAuthorInfo postAuthorInfo, PostContentInfo postContentInfo) {
        PostEditResult postEditResult = new PostEditResult();

        List<Attachment> attachments = postService.extractAttachmentsFromPost(postContentInfo.getBoardId(),
                extractBase64DataFromString(postContentInfo.getBody()));
        postService.setPostAuthor(postAuthorInfo, userInfo, getIp(request));
        postService.setPostContent(postContentInfo, attachments);

        if (!postService.checkValidations(postEditResult, postAuthorInfo, postContentInfo)) {
            return postEditResult;
        }

        postService.uploadPostExceptionHandler(postEditResult, attachments, postAuthorInfo, postContentInfo);

        return postEditResult;
    }


    @PostMapping(URLConst.CHECK_POST_PERMISSION)
    public SimpleResponse checkPostAccessPermission(HttpServletRequest request,
                                                  @Nullable @SessionAttribute(SessionConst.USER_INFO) UserInfo userInfo,
                                                  Long postId, String password) {
        SimpleResponse simpleResponse = postService.checkPostAccessPermission(userInfo, password, postId);
        if (simpleResponse.getSuccess()) {
            SessionManager.setAttributes(request, SessionConst.ACCESS_PERMITTED_POST_ID, postId);
        }
        return simpleResponse;
    }

    @PostMapping(URLConst.UPDATE_POST)
    public PostEditResult updatePost(HttpServletRequest request,
                                     @Nullable @SessionAttribute(SessionConst.USER_INFO) UserInfo userInfo,
                                     PostAuthorInfo postAuthorInfo, PostContentInfo postContentInfo)
            throws InvalidAccessException {
        PostEditResult postEditResult = new PostEditResult();


        Long permittedPostId = (Long) SessionManager.getAttributes(request, SessionConst.ACCESS_PERMITTED_POST_ID);
        if (permittedPostId == null || !permittedPostId.equals(postContentInfo.getPostId())) {
            throw new InvalidAccessException();
        }


        List<Attachment> attachments = postService.extractAttachmentsFromPost(postContentInfo.getBoardId(),
                extractBase64DataFromString(postContentInfo.getBody()));
        postService.setPostAuthor(postAuthorInfo, userInfo, getIp(request));
        postService.setPostContent(postContentInfo, attachments);


        if (!postService.checkValidations(postEditResult, postAuthorInfo, postContentInfo)) {
            return postEditResult;
        }


        // access validated post id session check
        postService.updatePostExceptionHandler(postEditResult, attachments, postAuthorInfo, postContentInfo);

        return postEditResult;
    }
}
