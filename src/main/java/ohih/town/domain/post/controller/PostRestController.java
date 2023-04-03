package ohih.town.domain.post.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.SessionConst;
import ohih.town.constants.URLConst;
import ohih.town.constants.UserConst;
import ohih.town.domain.post.dto.Attachment;
import ohih.town.domain.post.dto.PostAuthorInfo;
import ohih.town.domain.post.dto.PostContentInfo;
import ohih.town.domain.post.dto.UploadResult;
import ohih.town.domain.post.service.PostService;
import ohih.town.domain.user.dto.UserInfo;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

import static ohih.town.utilities.Utilities.extractAttachmentsFromBody;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PostRestController {

    private final PostService postService;

    @PostMapping(URLConst.UPLOAD_POST)
    public UploadResult uploadPost(HttpServletRequest request,
                                   @Nullable @SessionAttribute(SessionConst.USER_INFO) UserInfo userInfo,
                                   PostAuthorInfo postAuthorInfo, PostContentInfo postContentInfo) {
        postAuthorInfo.setUserId(null);
        postAuthorInfo.setIp("testIp");
        postAuthorInfo.setUserType(UserConst.USER_TYPE_GUEST);
        postAuthorInfo.setAuthor("testGuestUser");
        postAuthorInfo.setPassword("testPassword123");

        postContentInfo.setBoardId(1L);

        UploadResult uploadResult = new UploadResult();


        List<Attachment> attachments =
                postService.extractAttachmentsFromPost(
                        postContentInfo.getBoardId(), extractAttachmentsFromBody(postContentInfo.getBody()));

        postService.setPostAuthor(postAuthorInfo, userInfo);
        postService.setPostContent(postContentInfo, attachments);

        // Validate Fields
        if (!postService.checkValidations(uploadResult, postAuthorInfo, postContentInfo)) {
            return uploadResult;
        }


        // Upload post
        postService.uploadPostExceptionHandler(uploadResult, attachments, postAuthorInfo, postContentInfo);

        return uploadResult;
    }


}
