package ohih.town.domain.post.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ohih.town.ValidationResult;
import ohih.town.constants.*;
import ohih.town.domain.forum.service.ForumService;
import ohih.town.domain.post.dto.Attachment;
import ohih.town.domain.post.dto.UploadResult;
import ohih.town.domain.post.dto.PostContentInfo;
import ohih.town.domain.post.dto.PostAuthorInfo;
import ohih.town.domain.post.service.PostService;
import ohih.town.domain.user.dto.UserInfo;
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

@RestController
@RequiredArgsConstructor
public class PostRestController {

    private final ForumService forumService;
    private final PostService postService;

    @PostMapping(URLConst.UPLOAD_POST)
    public UploadResult uploadPost(HttpServletRequest request,
                                   @Nullable @SessionAttribute(SessionConst.USER_INFO) UserInfo userInfo,
                                   PostAuthorInfo postAuthorInfo, PostContentInfo postContentInfo) {
        UploadResult uploadResult = new UploadResult();
        List<Map<String, String>> errorMessages = new ArrayList<>();
        List<Map<String, Boolean>> fieldValidations = new ArrayList<>();
        boolean validPost = true;

        uploadResult.setSuccess(false);

        String boardName = forumService.getBoardNameById(postContentInfo.getBoardId());
        List<Attachment> attachments =
                postService.getAttachmentsFromPost(extractAttachmentsFromBody(postContentInfo.getBody()), boardName);
        postService.setAuthor(postAuthorInfo, userInfo, Utilities.getIp(request));
        postService.setContent(postContentInfo, attachments, boardName);


        // Validate Fields
        List<ValidationResult> validationResults = postService.checkValidations(postAuthorInfo, postContentInfo);

        for (ValidationResult validationResult : validationResults) {
            fieldValidations.add(validationResult.getFieldValidation());
            errorMessages.add(validationResult.getMessage());

            if (!validationResult.getIsValid()) {
                validPost = false;
            }
        }

        uploadResult.setFieldValidations(fieldValidations);
        uploadResult.setMessages(errorMessages);

        // If not valid any of fields return error messages with field name
        if (!validPost) {
            return uploadResult;
        }


        // Upload post
        Map<String, String> errorMessage = new HashMap<>();
        try {
            postService.uploadPost(attachments, postAuthorInfo, postContentInfo);
            uploadResult.setSuccess(true);
            uploadResult.setSuccessMessage(SUCCESS_MESSAGES.getString(POST_UPLOAD_SUCCESS));
        } catch (IOException e) {
            errorMessage.put(POST_UPLOAD_IO_EXCEPTION, POST_ERROR_MESSAGES.getString(POST_UPLOAD_IO_EXCEPTION));
        } catch (SQLException e) {
            errorMessage.put(POST_UPLOAD_SQL_EXCEPTION, POST_ERROR_MESSAGES.getString(POST_UPLOAD_SQL_EXCEPTION));
        } catch (NotAllowedExtensionException e) {
            errorMessage.put(UPLOAD_ATTACHMENT_EXTENSION_ERROR, POST_ERROR_MESSAGES.getString(UPLOAD_ATTACHMENT_EXTENSION_ERROR));
        }

        if (!errorMessage.isEmpty()) {
            errorMessages.add(errorMessage);
        }

        return uploadResult;
    }


}
