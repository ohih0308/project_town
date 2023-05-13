package ohih.town.domain.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.DomainConst;
import ohih.town.constants.ResourceBundleConst;
import ohih.town.constants.ValidationPatterns;
import ohih.town.domain.AccessInfo;
import ohih.town.domain.AccessPermissionCheckResult;
import ohih.town.domain.VerificationResult;
import ohih.town.domain.common.dto.AuthorInfo;
import ohih.town.domain.post.dto.*;
import ohih.town.domain.post.mapper.PostMapper;
import ohih.town.domain.user.dto.UserInfo;
import ohih.town.utilities.Utilities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;

import static ohih.town.constants.DateFormat.DATE_FORMAT_YYYY_MM_DD;
import static ohih.town.constants.DomainConst.USER_TYPE_GUEST;
import static ohih.town.constants.ErrorsConst.*;
import static ohih.town.constants.ResourceBundleConst.POST_ERROR_MESSAGES;
import static ohih.town.constants.ResourceBundleConst.SUCCESS_MESSAGES;
import static ohih.town.constants.SuccessConst.POST_ACCESS_PERMITTED;
import static ohih.town.constants.SuccessConst.POST_UPLOAD_SUCCESS;
import static ohih.town.constants.UtilityConst.UUID_FULL_INDEX;
import static ohih.town.utilities.Utilities.isValidated;

@RequiredArgsConstructor
@Slf4j
@Service
public class PostServiceImpl implements PostService {

    @Value("#{filePaths['post.attachments.directory']}")
    private String attachmentPath;

    private final PostMapper postMapper;

    @Override
    public AccessPermissionCheckResult checkAccessPermission(Long userId, Long postId, String password) {
        AccessPermissionCheckResult accessPermissionCheckResult = new AccessPermissionCheckResult();
        accessPermissionCheckResult.setId(postId);

        AccessInfo accessInfo = postMapper.getAccessInfo(postId);

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
    public VerificationResult verifyPostUploadRequest(PostUploadRequest postUploadRequest) {
        VerificationResult verificationResult = new VerificationResult();
        Map<String, String> messages = new HashMap<>();

        if (postUploadRequest.getAuthor() == null) {
            messages.put(DomainConst.AUTHOR, POST_ERROR_MESSAGES.getString(POST_AUTHOR_INVALID));
        }
        if (postUploadRequest.getPassword() == null) {
            messages.put(DomainConst.PASSWORD, POST_ERROR_MESSAGES.getString(POST_PASSWORD_INVALID));
        }
        if (postUploadRequest.getSubject() == null) {
            messages.put(DomainConst.SUBJECT, POST_ERROR_MESSAGES.getString(POST_SUBJECT_INVALID));
        }
        if (postUploadRequest.getBody() == null) {
            messages.put(DomainConst.BODY, POST_ERROR_MESSAGES.getString(POST_BODY_INVALID));
        }

        if (!messages.isEmpty()) {
            verificationResult.setMessages(messages);
            return verificationResult;
        }


        boolean authorValidation = isValidated(ValidationPatterns.USERNAME, postUploadRequest.getAuthor());
        boolean passwordValidation = isValidated(ValidationPatterns.GUEST_PASSWORD, postUploadRequest.getPassword());
        boolean subjectValidation = isValidated(ValidationPatterns.SUBJECT, postUploadRequest.getSubject());
        boolean bodyValidation = isValidated(ValidationPatterns.BODY, postUploadRequest.getBody());

        if (!authorValidation) {
            messages.put(DomainConst.AUTHOR, POST_ERROR_MESSAGES.getString(POST_AUTHOR_INVALID));
        }
        if (!passwordValidation) {
            messages.put(DomainConst.PASSWORD, POST_ERROR_MESSAGES.getString(POST_PASSWORD_INVALID));
        }
        if (!subjectValidation) {
            messages.put(DomainConst.SUBJECT, POST_ERROR_MESSAGES.getString(POST_SUBJECT_INVALID));
        }
        if (!bodyValidation) {
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
    public List<Attachment> extractAttachments(Long boardId, String body) {
        List<Attachment> attachments = new ArrayList<>();
        String date = Utilities.getDate(DATE_FORMAT_YYYY_MM_DD);

        List<String> extractedImages = Utilities.extractImages(body);

        for (String extractedImage : extractedImages) {
            String fileName = Utilities.createCode(UUID_FULL_INDEX);
            String extension = Utilities.extractExtension(extractedImage);
            Integer size = Utilities.getBytesLength(extractedImage);
            Path directory = Paths.get(attachmentPath, boardId.toString(), date).
                    resolve(fileName + "." + extension);

            Attachment attachment = Attachment.builder().
                    imageData(extractedImage.substring(extractedImage.indexOf(",") + 1)).
                    fileName(fileName).
                    extension(extension).
                    directory(directory.toString()).
                    size(size).build();

            attachments.add(attachment);
        }

        return attachments;
    }

    @Override
    public void setPostContent(PostContentInfo postContentInfo, List<Attachment> attachments) {

    }

    @Override
    public boolean uploadAttachments_prj(List<Attachment> attachments, Long postId) {
        try {
            for (Attachment attachment : attachments) {
                attachment.setPostId(postId);
                Utilities.isAllowedExtension(attachment.getExtension());
                Utilities.isFileSizeExceedLimit(attachment.getSize());

                Path path = Paths.get(attachment.getDirectory());

                File directory = new File(path.getParent().toString());

                if (!directory.exists()) {
                    if (!directory.mkdirs()) {
                        throw new IOException();
                    }
                }

                byte[] imageData = Base64.getDecoder().decode(attachment.getImageData());
                byte[] buffer = new byte[4096];
                int bytesRead;

                InputStream inputStream = new ByteArrayInputStream(imageData);
                FileOutputStream fileOutputStream = new FileOutputStream(attachment.getDirectory());

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                    fileOutputStream.flush();
                }
                fileOutputStream.close();
                inputStream.close();
            }
        } catch (Exception e) {
            log.info("{}", e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean uploadAttachments_db(List<Attachment> attachments, Long postId) {
        try {
            for (Attachment attachment : attachments) {
                attachment.setPostId(postId);
                if (!postMapper.uploadAttachment(attachment)) {
                    throw new SQLException();
                }
            }
        } catch (Exception e) {
            log.info("{}", e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public List<Attachment> getAttachments(Long postId) {
        return null;
    }

    @Override
    public void deleteAttachments(Long postId) {

    }

    @Override
    public boolean uploadThumbnail(Attachment attachment) throws SQLException {
        try {
            if (!postMapper.uploadThumbnail(attachment)) {
                throw new SQLException();
            }
        } catch (Exception e) {
            log.info("{}", e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public void deleteThumbnail(Long postId) {

    }

    @Override
    public boolean checkAccessPermission(UserInfo userInfo, String password, Long postId) {
        return false;
    }

    @Override
    public PostDetails getPostDetails(Long postId) {
        return null;
    }


    @Override
    @Transactional
    public PostUploadResult uploadPost(PostUploadRequest postUploadRequest, List<Attachment> attachments) {
        PostUploadResult postUploadResult = new PostUploadResult();

        VerificationResult verificationResult = verifyPostUploadRequest(postUploadRequest);
        if (!verificationResult.isVerified()) {
            postUploadResult.setErrorMessages(verificationResult.getMessages());
            postUploadResult.setResultMessage(POST_ERROR_MESSAGES.getString(POST_UPLOAD_FAILURE));
            return postUploadResult;
        }

        try {
            if (!postMapper.uploadPost(postUploadRequest) ||
                    !uploadAttachments_db(attachments, postUploadRequest.getPostId()) ||
                    !uploadThumbnail(attachments.get(0)) ||
                    !uploadAttachments_prj(attachments, postUploadRequest.getPostId())) {
                throw new Exception();
            }
            postUploadResult.setUploaded(true);
            postUploadResult.setResultMessage(SUCCESS_MESSAGES.getString(POST_UPLOAD_SUCCESS));
            postUploadResult.setPostId(postUploadResult.getPostId());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            postUploadResult.setResultMessage(POST_ERROR_MESSAGES.getString(POST_UPLOAD_FAILURE));
        }

        return postUploadResult;
    }

    @Override
    public void updatePost(List<Attachment> attachments, AuthorInfo authorInfo, PostContentInfo postContentInfo) {

    }

    @Override
    public void deletePost(Long postId) {

    }
}
