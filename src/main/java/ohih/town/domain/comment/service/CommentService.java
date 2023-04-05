package ohih.town.domain.comment.service;

import lombok.RequiredArgsConstructor;
import ohih.town.ValidationResult;
import ohih.town.constants.*;
import ohih.town.domain.SimpleResponse;
import ohih.town.domain.comment.dto.CommentAccessInfo;
import ohih.town.domain.comment.dto.CommentContentInfo;
import ohih.town.domain.comment.dto.CommentUploadRequest;
import ohih.town.domain.comment.mapper.CommentMapper;
import ohih.town.domain.common.dto.ActionResult;
import ohih.town.domain.common.dto.AuthorInfo;
import ohih.town.domain.common.service.CommonService;
import ohih.town.domain.notification.service.NotificationService;
import ohih.town.domain.post.dto.PostAccessInfo;
import ohih.town.domain.user.dto.UserInfo;
import ohih.town.exception.PartialDeleteException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;

import static ohih.town.constants.ErrorMessagesResourceBundle.*;
import static ohih.town.constants.ErrorsConst.*;
import static ohih.town.constants.ErrorsConst.POST_UPLOAD_IO_EXCEPTION;
import static ohih.town.constants.SuccessConst.*;
import static ohih.town.constants.SuccessMessagesResourceBundle.SUCCESS_MESSAGES;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommonService commonService;
    private final NotificationService notificationService;

    private final CommentMapper commentMapper;


    public boolean checkValidations(ActionResult actionResult,
                                    AuthorInfo authorInfo,
                                    String comment) {
        boolean isValid = true;

        ValidationResult[] validationResults = {
                commonService.checkValidation(ValidationPatterns.USERNAME,
                        USER_ERROR_MESSAGES, SUCCESS_MESSAGES,
                        USER_USERNAME_INVALID, USER_USERNAME_VALID,
                        PostConst.AUTHOR, authorInfo.getAuthor()),
                commonService.checkValidation(ValidationPatterns.PASSWORD,
                        USER_ERROR_MESSAGES, SUCCESS_MESSAGES,
                        USER_PASSWORD_INVALID, USER_PASSWORD_VALID,
                        PostConst.PASSWORD, authorInfo.getPassword()),
                commonService.checkValidation(ValidationPatterns.COMMENT,
                        COMMENT_ERROR_MESSAGES, SUCCESS_MESSAGES,
                        ErrorsConst.COMMENT_INVALID, SuccessConst.COMMENT_VALID,
                        CommentConst.COMMENT, comment)
        };

        List<Map<String, Boolean>> fieldValidations = new ArrayList<>();
        List<Map<String, String>> errorMessages = new ArrayList<>();

        for (ValidationResult validationResult : validationResults) {
            fieldValidations.add(validationResult.getFieldValidation());
            errorMessages.add(validationResult.getMessage());

            if (!validationResult.getIsValid()) {
                actionResult.setFieldValidations(fieldValidations);
                actionResult.setErrorMessages(errorMessages);
                isValid = false;
            }
        }
        return isValid;
    }


    private void uploadComment(AuthorInfo authorInfo, CommentContentInfo commentContentInfo) throws SQLException {
        CommentUploadRequest commentUploadRequest = new CommentUploadRequest(authorInfo, commentContentInfo);

        if (!commentMapper.uploadComment(commentUploadRequest)) {
            throw new SQLException();
        }
    }


    public CommentAccessInfo getCommentAccessInfoByCommentId(Long commentId) {
        return commentMapper.getCommentAccessInfoByCommentId(commentId);
    }

    public SimpleResponse checkPostAccessPermission(UserInfo userInfo, String password, Long commentId) {
        SimpleResponse simpleResponse = new SimpleResponse();
        CommentAccessInfo commentAccessInfo = getCommentAccessInfoByCommentId(commentId);
        simpleResponse.setSuccess(false);

        if (commentAccessInfo == null) {
            simpleResponse.setMessage(COMMENT_ERROR_MESSAGES.getString(COMMENT_EXISTENCE_ERROR));
            return simpleResponse;
        }

        if (commentAccessInfo.getUserType().equals(UserConst.USER_TYPE_GUEST)) {
            if (Objects.equals(commentAccessInfo.getPassword(), (password))) {
                simpleResponse.setSuccess(true);
            } else {
                simpleResponse.setMessage(COMMENT_ERROR_MESSAGES.getString(COMMENT_PERMISSION_ERROR));
            }
        } else if (commentAccessInfo.getUserType().equals(UserConst.USER_TYPE_MEMBER)) {
            if (userInfo == null
                    || commentAccessInfo.getUserId().equals(userInfo.getUserId())) {
                simpleResponse.setMessage(COMMENT_ERROR_MESSAGES.getString(COMMENT_PERMISSION_ERROR));
            } else {
                simpleResponse.setSuccess(true);
            }
        }

        return simpleResponse;
    }

    private void deleteComment(Long commentId) throws SQLException {
        if (!commentMapper.deleteComment(commentId)) {
            throw new SQLException();
        }
    }

    private Integer getTotalCommentCountByPostId(Long postId) {
        return commentMapper.getTotalCommentCountByPostId(postId);
    }

    private List<Long> getCommentIdsByPostId(Long postId) {
        return commentMapper.getCommentIdsByPostId(postId);
    }

    public void deleteCommentsByPostId(Long postId) throws PartialDeleteException {
        int totalCommentCount = getTotalCommentCountByPostId(postId);

        List<Long> commentIds = getCommentIdsByPostId(postId);

        for (Long commentId : commentIds) {
            try {
                deleteComment(commentId);
            } catch (SQLException e) {
                throw new PartialDeleteException();
            }
        }
    }

    public void uploadCommentExceptionHandler(ActionResult actionResult,
                                              AuthorInfo authorInfo, CommentContentInfo commentContentInfo) {
        try {
            uploadComment(authorInfo, commentContentInfo);
            actionResult.setSuccess(true);
            actionResult.setSuccessMessage(SUCCESS_MESSAGES.getString(COMMENT_UPLOAD_SUCCESS));
        } catch (SQLException e) {
            Map<String, String> errorMessage = Collections.singletonMap(
                    COMMENT_UPLOAD_SQL_EXCEPTION, COMMENT_ERROR_MESSAGES.getString(COMMENT_UPLOAD_SQL_EXCEPTION));
            actionResult.setErrorMessages(Collections.singletonList(errorMessage));
        }

        notificationService.createPostCommentNotification(commentContentInfo.getPostId());
    }

    public void deleteCommentExceptionHandler(ActionResult actionResult,
                                              Long commentId) {
        try {
            deleteComment(commentId);
            actionResult.setSuccess(true);
            actionResult.setSuccessMessage(SUCCESS_MESSAGES.getString(COMMENT_DELETE_SUCCESS));
        } catch (SQLException e) {
            Map<String, String> errorMessage = Collections.singletonMap(
                    COMMENT_DELETE_SQL_EXCEPTION, COMMENT_ERROR_MESSAGES.getString(COMMENT_DELETE_SQL_EXCEPTION));
            actionResult.setErrorMessages(Collections.singletonList(errorMessage));
        }
    }
}
