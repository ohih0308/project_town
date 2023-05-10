package ohih.town.domain.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.*;
import ohih.town.domain.AccessPermissionCheckResult;
import ohih.town.domain.VerificationResult;
import ohih.town.domain.comment.dto.CommentAccessInfo;
import ohih.town.domain.comment.dto.CommentDeleteResult;
import ohih.town.domain.comment.dto.CommentUploadRequest;
import ohih.town.domain.comment.dto.CommentUploadResult;
import ohih.town.domain.comment.mapper.CommentMapper;
import ohih.town.utilities.Utilities;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static ohih.town.constants.DomainConst.USER_TYPE_GUEST;
import static ohih.town.constants.ErrorsConst.COMMENT_ACCESS_DENIED;
import static ohih.town.constants.ResourceBundleConst.SUCCESS_MESSAGES;
import static ohih.town.constants.SuccessConst.COMMENT_ACCESS_PERMITTED;
import static ohih.town.domain.user.service.UserService.*;

@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;


    @Override
    public VerificationResult verifyCommentUploadRequest(CommentUploadRequest commentUploadRequest) {
        VerificationResult verificationResult = new VerificationResult();
        Map<String, String> messages = new HashMap<>();

        if (!commentMapper.isPostIdExists(commentUploadRequest.getPostId())) {
            messages.put(DomainConst.POST_ID, commentErrorMessageSource.getString(ErrorsConst.COMMENT_POST_ID_INVALID));
        }
        if (commentUploadRequest.getAuthor() == null) {
            messages.put(DomainConst.AUTHOR, commentErrorMessageSource.getString(ErrorsConst.COMMENT_AUTHOR_INVALID));
        }
        if (commentUploadRequest.getPassword() == null) {
            messages.put(DomainConst.PASSWORD, commentErrorMessageSource.getString(ErrorsConst.COMMENT_PASSWORD_INVALID));
        }
        if (commentUploadRequest.getComment() == null) {
            messages.put(UtilityConst.COMMENT, commentErrorMessageSource.getString(ErrorsConst.COMMENT_COMMENT_INVALID));
        }

        if (!messages.isEmpty()) {
            verificationResult.setMessages(messages);
            return verificationResult;
        }


        boolean authorValidation = Utilities.isValidated(ValidationPatterns.USERNAME, commentUploadRequest.getAuthor());
        boolean passwordValidation = Utilities.isValidated(ValidationPatterns.GUEST_PASSWORD, commentUploadRequest.getPassword());
        boolean commentValidation = Utilities.isValidated(ValidationPatterns.COMMENT, commentUploadRequest.getComment());

        if (!authorValidation) {
            messages.put(DomainConst.AUTHOR, commentErrorMessageSource.getString(ErrorsConst.COMMENT_AUTHOR_INVALID));
        }
        if (!passwordValidation) {
            messages.put(DomainConst.PASSWORD, commentErrorMessageSource.getString(ErrorsConst.COMMENT_PASSWORD_INVALID));
        }
        if (!commentValidation) {
            messages.put(UtilityConst.COMMENT, commentErrorMessageSource.getString(ErrorsConst.COMMENT_COMMENT_INVALID));
        }

        if (messages.isEmpty()) {
            verificationResult.setVerified(true);
        } else {
            verificationResult.setMessages(messages);
        }

        return verificationResult;
    }

    @Override
    public CommentUploadResult uploadComment(CommentUploadRequest commentUploadRequest) {
        CommentUploadResult commentUploadResult = new CommentUploadResult();
        VerificationResult verificationResult = verifyCommentUploadRequest(commentUploadRequest);

        if (!verificationResult.isVerified()) {
            commentUploadResult.setErrorMessages(verificationResult.getMessages());
            commentUploadResult.setResultMessage(commentErrorMessageSource.getString(ErrorsConst.COMMENT_UPLOAD_FAILURE));
            return commentUploadResult;
        }

        try {
            if (!commentMapper.uploadComment(commentUploadRequest)) {
                throw new SQLException();
            }
            commentUploadResult.setUploaded(true);
            commentUploadResult.setResultMessage(SUCCESS_MESSAGES.getString(SuccessConst.COMMENT_UPLOAD_SUCCESS));
        } catch (Exception e) {
            log.info("{}", e.getMessage());
            commentUploadResult.setResultMessage(commentErrorMessageSource.getString(ErrorsConst.COMMENT_UPLOAD_FAILURE));
        }

        return commentUploadResult;
    }

    @Override
    public AccessPermissionCheckResult checkAccessPermission(Long userId, Long commentId, String password) {
        AccessPermissionCheckResult accessPermissionCheckResult = new AccessPermissionCheckResult();
        accessPermissionCheckResult.setId(commentId);

        CommentAccessInfo commentAccessInfo = commentMapper.getCommentAccessInfoByCommentId(commentId);

        boolean isAccessible = false;
        accessPermissionCheckResult.setMessage(commentErrorMessageSource.getString(COMMENT_ACCESS_DENIED));

        if (Objects.equals(commentAccessInfo.getUserType(), USER_TYPE_GUEST)) {
            if (Objects.equals(commentAccessInfo.getPassword(), password)) {
                isAccessible = true;
            }
        } else {
            if (Objects.equals(commentAccessInfo.getUserId(), userId)) {
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
    public CommentDeleteResult deleteComment(Long accessPermittedCommentId, Long commentId) {
        CommentDeleteResult commentDeleteResult = new CommentDeleteResult();
        commentDeleteResult.setCommentId(commentId);

        if (accessPermittedCommentId == null || !accessPermittedCommentId.equals(commentId)) {
            commentDeleteResult.setMessage(commentErrorMessageSource.getString(COMMENT_ACCESS_DENIED));
            return commentDeleteResult;
        }

        try {
            if (!commentMapper.deleteComment(commentId)) {
                throw new SQLException();
            }
            commentDeleteResult.setDeleted(true);
            commentDeleteResult.setMessage(successMessageSource.getString(SuccessConst.COMMENT_DELETE_SUCCESS));
        } catch (Exception e) {
            log.info("{}", e.getMessage());
            commentDeleteResult.setMessage(commentErrorMessageSource.getString(ErrorsConst.COMMENT_DELETE_FAILURE));
        }

        return commentDeleteResult;
    }

    @Override
    public void deleteCommentsByPostId(Long postId) {

    }


    @Override
    public Integer getTotalCountsByPostId(Long postId) {
        return null;
    }
}
