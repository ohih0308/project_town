package ohih.town.domain.guestbook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.DomainConst;
import ohih.town.constants.ErrorsConst;
import ohih.town.constants.ResourceBundleConst;
import ohih.town.constants.ValidationPatterns;
import ohih.town.domain.AccessInfo;
import ohih.town.domain.AccessPermissionCheckResult;
import ohih.town.domain.VerificationResult;
import ohih.town.domain.common.dto.AuthorInfo;
import ohih.town.domain.guestbook.dto.ContentInfo;
import ohih.town.domain.guestbook.dto.PostUploadRequest;
import ohih.town.domain.guestbook.dto.GuestbookResult;
import ohih.town.domain.guestbook.dto.GuestbookWriteConfig;
import ohih.town.domain.guestbook.mapper.GuestbookMapper;
import ohih.town.domain.post.dto.PostResult;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

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
    public AccessPermissionCheckResult checkAccessPermission(Long userId, Long postId, String password) {
        AccessPermissionCheckResult accessPermissionCheckResult = new AccessPermissionCheckResult();
        accessPermissionCheckResult.setId(postId);

        AccessInfo accessInfo = guestbookMapper.getAccessInfo(postId);

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
            PostUploadRequest postUploadRequest = new PostUploadRequest(authorInfo, contentInfo);
            if (!guestbookMapper.uploadPost(postUploadRequest)) {
                throw new SQLException();
            }
            postResult.setSuccess(true);
            postResult.setResultMessage(SUCCESS_MESSAGES.getString(POST_UPLOAD_SUCCESS));
            postResult.setPostId(postUploadRequest.getPostId());
        } catch (SQLException e) {
            log.info("{}", e.getMessage());
            postResult.setResultMessage(POST_ERROR_MESSAGES.getString(POST_UPLOAD_FAILURE));
        }

        return postResult;
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


}
