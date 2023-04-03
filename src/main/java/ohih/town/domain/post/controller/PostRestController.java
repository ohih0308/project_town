package ohih.town.domain.post.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.ValidationResult;
import ohih.town.constants.*;
import ohih.town.domain.forum.service.ForumService;
import ohih.town.domain.post.dto.Attachment;
import ohih.town.domain.post.dto.UploadResult;
import ohih.town.domain.post.dto.PostContentInfo;
import ohih.town.domain.post.dto.PostAuthorInfo;
import ohih.town.domain.post.service.PostService;
import ohih.town.domain.user.dto.UserInfo;
import ohih.town.exception.FileSizeExceedLimitException;
import ohih.town.exception.NotAllowedExtensionException;
import ohih.town.utilities.Utilities;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ohih.town.constants.ErrorMessagesResourceBundle.POST_ERROR_MESSAGES;
import static ohih.town.constants.ErrorsConst.*;
import static ohih.town.constants.SuccessConst.POST_UPLOAD_SUCCESS;
import static ohih.town.constants.SuccessMessagesResourceBundle.SUCCESS_MESSAGES;
import static ohih.town.utilities.Utilities.extractAttachmentsFromBody;
import static ohih.town.utilities.Utilities.getIp;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PostRestController {

    private final ForumService forumService;
    private final PostService postService;

    @PostMapping(URLConst.UPLOAD_POST)
    public UploadResult uploadPost(HttpServletRequest request,
                                   @Nullable @SessionAttribute(SessionConst.USER_INFO) UserInfo userInfo,
                                   PostAuthorInfo postAuthorInfo, PostContentInfo postContentInfo) {
        UploadResult uploadResult = new UploadResult();
        List<Map<String, String>> errorMessages = new ArrayList<>();


        List<Attachment> attachments =
                postService.extractAttachmentsFromPost(
                        postContentInfo.getBoardId(), extractAttachmentsFromBody(postContentInfo.getBody()));

        postService.setPostAuthor(postAuthorInfo, userInfo);
        postService.setPostContent(postContentInfo, attachments);

        // Validate Fields
        if (!postService.checkValidations(uploadResult, errorMessages, postAuthorInfo, postContentInfo)) {
            return uploadResult;
        }


        // Upload post
        postService.uploadPostExceptionHandler(uploadResult, errorMessages,
                attachments, postAuthorInfo, postContentInfo);

        return uploadResult;
    }


}
