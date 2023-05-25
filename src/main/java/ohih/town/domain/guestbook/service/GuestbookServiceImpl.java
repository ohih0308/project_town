package ohih.town.domain.guestbook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.*;
import ohih.town.domain.AccessInfo;
import ohih.town.domain.AccessPermissionCheckResult;
import ohih.town.domain.VerificationResult;
import ohih.town.domain.comment.dto.CommentResult;
import ohih.town.domain.common.dto.AuthorInfo;
import ohih.town.domain.guestbook.dto.*;
import ohih.town.domain.guestbook.mapper.GuestbookMapper;
import ohih.town.domain.post.dto.PostResult;
import ohih.town.utilities.Paging;
import ohih.town.utilities.Search;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;

import static ohih.town.constants.DomainConst.USER_TYPE_GUEST;
import static ohih.town.constants.ErrorsConst.*;
import static ohih.town.constants.ResourceBundleConst.POST_ERROR_MESSAGES;
import static ohih.town.constants.ResourceBundleConst.SUCCESS_MESSAGES;
import static ohih.town.constants.SuccessConst.*;
import static ohih.town.utilities.Utilities.isValidated;

@Service
@Slf4j
@RequiredArgsConstructor
public class GuestbookServiceImpl implements GuestbookService {

    private final GuestbookMapper guestbookMapper;

    ResourceBundle guestbookErrorMessages = ResourceBundleConst.GUESTBOOK_ERROR_MESSAGES;

    @Override
    public AccessPermissionCheckResult checkPostAccessPermission(Long userId, Long postId, String password) {
        AccessPermissionCheckResult accessPermissionCheckResult = new AccessPermissionCheckResult();
        accessPermissionCheckResult.setId(postId);

        AccessInfo accessInfo = guestbookMapper.getPostAccessInfo(postId);

        boolean isAccessible = false;
        accessPermissionCheckResult.setMessage(POST_ERROR_MESSAGES.getString(POST_ACCESS_DENIED));

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
            accessPermissionCheckResult.setMessage(SUCCESS_MESSAGES.getString(POST_ACCESS_PERMITTED));
        }

        return accessPermissionCheckResult;
    }

    @Override
    public AccessPermissionCheckResult checkCommentAccessPermission(Long userId, Long commentId, String password) {
        AccessPermissionCheckResult accessPermissionCheckResult = new AccessPermissionCheckResult();
        accessPermissionCheckResult.setId(commentId);

        AccessInfo accessInfo = guestbookMapper.getCommentAccessInfo(commentId);

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
    public GuestbookResult checkGuestbookConfigs(Long ownerId, AuthorInfo authorInfo) {
        GuestbookResult guestbookResult = new GuestbookResult();
        guestbookResult.setOwnerId(ownerId);

        GuestbookWriteConfig writeConfig = guestbookMapper.getGuestbookWriteConfig(ownerId);
        boolean isGuest = Objects.equals(authorInfo.getUserType(), DomainConst.USER_TYPE_GUEST);

        if (!writeConfig.isActivation()) {
            guestbookResult.setMessage(guestbookErrorMessages.getString(ErrorsConst.GUESTBOOK_ACCESS_DISABLED));
        } else if (isGuest && !writeConfig.isGuestWrite()) {
            guestbookResult.setMessage(guestbookErrorMessages.getString(ErrorsConst.GUESTBOOK_ACCESS_GUEST_NOT_ALLOWED));
        } else if (!isGuest && !writeConfig.isMemberWrite()) {
            guestbookResult.setMessage(guestbookErrorMessages.getString(ErrorsConst.GUESTBOOK_ACCESS_MEMBER_NOT_ALLOWED));
        } else {
            guestbookResult.setSuccess(true);
        }

        return guestbookResult;
    }

    @Override
    public VerificationResult verifyPostUploadRequest(AuthorInfo authorInfo, ContentInfo contentInfo) {
        VerificationResult verificationResult = new VerificationResult();
        Map<String, String> messages = new HashMap<>();

        if (authorInfo.getAuthor() == null) {
            messages.put(DomainConst.AUTHOR, POST_ERROR_MESSAGES.getString(POST_AUTHOR_INVALID));
        }
        if (authorInfo.getPassword() == null) {
            messages.put(DomainConst.PASSWORD, POST_ERROR_MESSAGES.getString(POST_PASSWORD_INVALID));
        }
        if (contentInfo.getContent() == null) {
            messages.put(DomainConst.BODY, POST_ERROR_MESSAGES.getString(POST_BODY_INVALID));
        }

        if (!messages.isEmpty()) {
            verificationResult.setMessages(messages);
            return verificationResult;
        }


        boolean authorValidation = isValidated(ValidationPatterns.USERNAME, authorInfo.getAuthor());
        boolean passwordValidation = isValidated(ValidationPatterns.GUEST_PASSWORD, authorInfo.getPassword());
        boolean contentValidation = isValidated(ValidationPatterns.BODY, contentInfo.getContent());

        if (!authorValidation) {
            messages.put(DomainConst.AUTHOR, POST_ERROR_MESSAGES.getString(POST_AUTHOR_INVALID));
        }
        if (!passwordValidation) {
            messages.put(DomainConst.PASSWORD, POST_ERROR_MESSAGES.getString(POST_PASSWORD_INVALID));
        }
        if (!contentValidation) {
            messages.put(DomainConst.BODY, POST_ERROR_MESSAGES.getString(POST_BODY_INVALID));
        }

        if (messages.isEmpty()) {
            verificationResult.setVerified(true);
        } else {
            verificationResult.setMessages(messages);
        }

        return verificationResult;
    }

    @Override
    public Long countPosts(Long userId, Search search) {
        Map<String, Object> map = new HashMap<>();
        map.put(DomainConst.USER_ID, userId);
        map.put(UtilityConst.SEARCH, search);

        return guestbookMapper.countPosts(map);
    }

    @Override
    public List<Guestbook> getPosts(Long ownerId, Long userId, Paging paging) {
        Map<String, Object> map = new HashMap<>();
        map.put(DomainConst.USER_ID, ownerId);
        map.put(UtilityConst.PAGING, paging);

        if (Objects.equals(ownerId, userId)) {
            return guestbookMapper.getAllPosts(map);
        } else {
            return guestbookMapper.getPublicPosts(map);
        }
    }

    @Override
    public Long countComments(Long postId) {
        return guestbookMapper.countComments(postId);
    }

    @Override
    public boolean isGuestbookOwner(Long postId, Long userId) {
        return Objects.equals(guestbookMapper.getGuestbookOwner(postId), userId);
    }

    @Override
    public List<Guestbook> getComments(Long postId, Long userId, Paging paging) {
        boolean isPrivatePost = guestbookMapper.isPrivatePost(postId);
        boolean isOwner = isGuestbookOwner(postId, userId);

        Map<String, Object> map = new HashMap<>();
        map.put(DomainConst.POST_ID, postId);
        map.put(UtilityConst.PAGING, paging);

        if (!isPrivatePost || isOwner) {
            return guestbookMapper.getComments(map);
        } else {
            return null;
        }
    }


    @Override
    public PostResult uploadPost(AuthorInfo authorInfo, ContentInfo contentInfo) {
        PostResult postResult = new PostResult();

        GuestbookResult guestbookResult = checkGuestbookConfigs(contentInfo.getOwnerId(), authorInfo);

        if (!guestbookResult.isSuccess()) {
            postResult.setResultMessage(guestbookResult.getMessage());
            return postResult;
        }

        VerificationResult verificationResult = verifyPostUploadRequest(authorInfo, contentInfo);
        if (!verificationResult.isVerified()) {
            postResult.setErrorMessages(verificationResult.getMessages());
            postResult.setResultMessage(POST_ERROR_MESSAGES.getString(POST_UPLOAD_FAILURE));
            return postResult;
        }

        try {
            GuestbookUploadRequest guestbookUploadRequest = new GuestbookUploadRequest(authorInfo, contentInfo);
            if (!guestbookMapper.uploadPost(guestbookUploadRequest)) {
                throw new SQLException();
            }
            postResult.setSuccess(true);
            postResult.setResultMessage(SUCCESS_MESSAGES.getString(POST_UPLOAD_SUCCESS));
            postResult.setPostId(guestbookUploadRequest.getContentId());
        } catch (SQLException e) {
            log.info("{}", e.getMessage());
            postResult.setResultMessage(POST_ERROR_MESSAGES.getString(POST_UPLOAD_FAILURE));
        }

        return postResult;
    }

    @Override
    public CommentResult uploadComment(AuthorInfo authorInfo, ContentInfo contentInfo) {
        CommentResult commentResult = new CommentResult();
        VerificationResult verificationResult = verifyPostUploadRequest(authorInfo, contentInfo);

        if (!verificationResult.isVerified()) {
            commentResult.setErrorMessages(verificationResult.getMessages());
            commentResult.setResultMessage(ResourceBundleConst.COMMENT_ERROR_MESSAGES.getString(ErrorsConst.COMMENT_UPLOAD_FAILURE));
            return commentResult;
        }

        try {
            GuestbookUploadRequest guestbookUploadRequest = new GuestbookUploadRequest(authorInfo, contentInfo);
            if (!guestbookMapper.uploadComment(guestbookUploadRequest)) {
                throw new SQLException();
            }
            commentResult.setSuccess(true);
            commentResult.setResultMessage(SUCCESS_MESSAGES.getString(SuccessConst.COMMENT_UPLOAD_SUCCESS));
            commentResult.setCommentId(guestbookUploadRequest.getContentId());
        } catch (SQLException e) {
            log.info("{}", e.getMessage());
            commentResult.setResultMessage(ResourceBundleConst.COMMENT_ERROR_MESSAGES.getString(ErrorsConst.COMMENT_UPLOAD_FAILURE));
        }

        return commentResult;
    }

    @Override
    public PostResult deletePost(Long accessPermittedPostId, Long postId) {
        PostResult postResult = new PostResult();

        if (!Objects.equals(accessPermittedPostId, postId)) {
            postResult.setResultMessage(POST_ERROR_MESSAGES.getString(POST_ACCESS_DENIED));
            return postResult;
        }

        try {
            if (!guestbookMapper.deletePost(postId)) {
                throw new SQLException();
            }
            postResult.setSuccess(true);
            postResult.setPostId(postId);
            postResult.setResultMessage(SUCCESS_MESSAGES.getString(POST_DELETE_SUCCESS));
        } catch (SQLException e) {
            log.info("{}", e.getMessage());
            postResult.setResultMessage(POST_ERROR_MESSAGES.getString(POST_DELETE_FAILURE));
        }
        return postResult;
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
            if (!guestbookMapper.deleteComment(commentId)) {
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
