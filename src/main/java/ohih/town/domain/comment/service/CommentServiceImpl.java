package ohih.town.domain.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.*;
import ohih.town.domain.AccessPermissionCheckResult;
import ohih.town.domain.VerificationResult;
import ohih.town.domain.AccessInfo;
import ohih.town.domain.comment.dto.CommentUploadRequest;
import ohih.town.domain.comment.dto.CommentResult;
import ohih.town.domain.comment.mapper.CommentMapper;
import ohih.town.utilities.Utilities;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static ohih.town.constants.DomainConst.USER_TYPE_GUEST;
import static ohih.town.constants.ErrorsConst.COMMENT_ACCESS_DENIED;
import static ohih.town.constants.ResourceBundleConst.SUCCESS_MESSAGES;
import static ohih.town.constants.SuccessConst.COMMENT_ACCESS_PERMITTED;

@RequiredArgsConstructor
@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;


    @Override
    public VerificationResult verifyCommentUploadRequest(CommentUploadRequest commentUploadRequest) {
        VerificationResult verificationResult = new VerificationResult();
        Map<String, String> messages = new HashMap<>();

        if (!commentMapper.isPostIdExists(commentUploadRequest.getPostId())) {
            messages.put(DomainConst.POST_ID, ResourceBundleConst.COMMENT_ERROR_MESSAGES.getString(ErrorsConst.COMMENT_POST_ID_INVALID));
        }
        if (commentUploadRequest.getAuthor() == null) {
            messages.put(DomainConst.AUTHOR, ResourceBundleConst.COMMENT_ERROR_MESSAGES.getString(ErrorsConst.COMMENT_AUTHOR_INVALID));
        }
        if (commentUploadRequest.getPassword() == null) {
            messages.put(DomainConst.PASSWORD, ResourceBundleConst.COMMENT_ERROR_MESSAGES.getString(ErrorsConst.COMMENT_PASSWORD_INVALID));
        }
        if (commentUploadRequest.getComment() == null) {
            messages.put(UtilityConst.COMMENT, ResourceBundleConst.COMMENT_ERROR_MESSAGES.getString(ErrorsConst.COMMENT_COMMENT_INVALID));
        }

        if (!messages.isEmpty()) {
            verificationResult.setMessages(messages);
            return verificationResult;
        }


        boolean authorValidation = Utilities.isValidated(ValidationPatterns.USERNAME, commentUploadRequest.getAuthor());
        boolean passwordValidation = Utilities.isValidated(ValidationPatterns.GUEST_PASSWORD, commentUploadRequest.getPassword());
        boolean commentValidation = Utilities.isValidated(ValidationPatterns.COMMENT, commentUploadRequest.getComment());

        if (!authorValidation) {
            messages.put(DomainConst.AUTHOR, ResourceBundleConst.COMMENT_ERROR_MESSAGES.getString(ErrorsConst.COMMENT_AUTHOR_INVALID));
        }
        if (!passwordValidation) {
            messages.put(DomainConst.PASSWORD, ResourceBundleConst.COMMENT_ERROR_MESSAGES.getString(ErrorsConst.COMMENT_PASSWORD_INVALID));
        }
        if (!commentValidation) {
            messages.put(UtilityConst.COMMENT, ResourceBundleConst.COMMENT_ERROR_MESSAGES.getString(ErrorsConst.COMMENT_COMMENT_INVALID));
        }

        if (messages.isEmpty()) {
            verificationResult.setVerified(true);
        } else {
            verificationResult.setMessages(messages);
        }

        return verificationResult;
    }

    @Override
    public CommentResult uploadComment(CommentUploadRequest commentUploadRequest) {
        CommentResult commentResult = new CommentResult();
        VerificationResult verificationResult = verifyCommentUploadRequest(commentUploadRequest);

        if (!verificationResult.isVerified()) {
            commentResult.setErrorMessages(verificationResult.getMessages());
            commentResult.setResultMessage(ResourceBundleConst.COMMENT_ERROR_MESSAGES.getString(ErrorsConst.COMMENT_UPLOAD_FAILURE));
            return commentResult;
        }

        try {
            if (!commentMapper.uploadComment(commentUploadRequest)) {
                throw new SQLException();
            }
            commentResult.setSuccess(true);
            commentResult.setResultMessage(SUCCESS_MESSAGES.getString(SuccessConst.COMMENT_UPLOAD_SUCCESS));
        } catch (Exception e) {
            log.info("{}", e.getMessage());
            commentResult.setResultMessage(ResourceBundleConst.COMMENT_ERROR_MESSAGES.getString(ErrorsConst.COMMENT_UPLOAD_FAILURE));
        }

        return commentResult;
    }

    @Override
    public AccessPermissionCheckResult checkAccessPermission(Long userId, Long commentId, String password) {
        AccessPermissionCheckResult accessPermissionCheckResult = new AccessPermissionCheckResult();
        accessPermissionCheckResult.setId(commentId);

        AccessInfo accessInfo = commentMapper.getAccessInfo(commentId);

        boolean isAccessible = false;
        accessPermissionCheckResult.setMessage(ResourceBundleConst.COMMENT_ERROR_MESSAGES.getString(COMMENT_ACCESS_DENIED));

        if (Objects.equals(accessInfo.getUserType(), USER_TYPE_GUEST)) {
            if (Objects.equals(accessInfo.getPassword(), password)) {
                isAccessible = true;
            }
        } else {
            if (Objects.equals(accessInfo.getUserId(), userId)) {
                isAccessible = true;
            }
        }

        if (isAccessible) {
            accessPermissionCheckResult.setAccessible(true);
            accessPermissionCheckResult.setMessage(SUCCESS_MESSAGES.getString(COMMENT_ACCESS_PERMITTED));
        }

        return accessPermissionCheckResult;
    }

    @Override
    public CommentResult deleteComment(Long accessPermittedCommentId, Long commentId) {
        CommentResult commentResult = new CommentResult();
        commentResult.setCommentId(commentId);

        if (accessPermittedCommentId == null || !accessPermittedCommentId.equals(commentId)) {
            commentResult.setResultMessage(ResourceBundleConst.COMMENT_ERROR_MESSAGES.getString(COMMENT_ACCESS_DENIED));
            return commentResult;
        }

        try {
            if (!commentMapper.deleteComment(commentId)) {
                throw new SQLException();
            }
            commentResult.setSuccess(true);
            commentResult.setResultMessage(SUCCESS_MESSAGES.getString(SuccessConst.COMMENT_DELETE_SUCCESS));
        } catch (Exception e) {
            log.info("{}", e.getMessage());
            commentResult.setResultMessage(ResourceBundleConst.COMMENT_ERROR_MESSAGES.getString(ErrorsConst.COMMENT_DELETE_FAILURE));
        }

        return commentResult;
    }
}
