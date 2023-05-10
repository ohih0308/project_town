package ohih.town.domain.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.DomainConst;
import ohih.town.constants.ValidationPatterns;
import ohih.town.domain.VerificationResult;
import ohih.town.domain.common.dto.AuthorInfo;
import ohih.town.domain.post.dto.*;
import ohih.town.domain.post.mapper.PostMapper;
import ohih.town.domain.user.dto.UserInfo;
import ohih.town.utilities.Utilities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;

import static ohih.town.constants.DateFormat.DATE_FORMAT_YYYY_MM_DD;
import static ohih.town.constants.ErrorsConst.*;
import static ohih.town.constants.UtilityConst.UUID_FULL_INDEX;
import static ohih.town.utilities.Utilities.isValidated;

@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    @Value("#{filePaths['post.attachments.directory']}")
    private String attachmentPath;

    private final PostMapper postMapper;


    @Override
    public VerificationResult verifyPostUploadRequest(PostUploadRequest postUploadRequest) {
        VerificationResult verificationResult = new VerificationResult();
        Map<String, String> messages = new HashMap<>();

        if (postUploadRequest.getAuthor() == null) {
            messages.put(DomainConst.AUTHOR, postErrorMessageSource.getString(POST_AUTHOR_INVALID));
        }
        if (postUploadRequest.getPassword() == null) {
            messages.put(DomainConst.PASSWORD, postErrorMessageSource.getString(POST_PASSWORD_INVALID));
        }
        if (postUploadRequest.getSubject() == null) {
            messages.put(DomainConst.SUBJECT, postErrorMessageSource.getString(POST_SUBJECT_INVALID));
        }
        if (postUploadRequest.getBody() == null) {
            messages.put(DomainConst.BODY, postErrorMessageSource.getString(POST_BODY_INVALID));
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
            messages.put(DomainConst.AUTHOR, postErrorMessageSource.getString(POST_AUTHOR_INVALID));
        }
        if (!passwordValidation) {
            messages.put(DomainConst.PASSWORD, postErrorMessageSource.getString(POST_PASSWORD_INVALID));
        }
        if (!subjectValidation) {
            messages.put(DomainConst.SUBJECT, postErrorMessageSource.getString(POST_SUBJECT_INVALID));
        }
        if (!bodyValidation) {
            messages.put(DomainConst.BODY, postErrorMessageSource.getString(POST_BODY_INVALID));
        }

        if (messages.isEmpty()) {
            verificationResult.setVerified(true);
        } else {
            verificationResult.setMessages(messages);
        }

        return verificationResult;
    }

    @Override
    public List<Attachment> extractAttachments(Long boardId, Long postId, String body) {
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
                    size(size).
                    postId(postId).build();

            attachments.add(attachment);
        }

        return attachments;
    }

    @Override
    public void setPostContent(PostContentInfo postContentInfo, List<Attachment> attachments) {

    }

    @Override
    public void uploadAttachments(List<Attachment> attachments) throws IOException, SQLException {
        for (Attachment attachment : attachments) {
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


            if (!postMapper.uploadAttachment(attachment)) {
                throw new SQLException();
            }
        }
    }

    @Override
    public List<Attachment> getAttachments(Long postId) {
        return null;
    }

    @Override
    public void deleteAttachments(Long postId) {

    }

    @Override
    public boolean uploadThumbnail(Attachment attachment) {
        return postMapper.uploadThumbnail(attachment);
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
    public PostUploadResult uploadPost(PostUploadRequest postUploadRequest) {
        PostUploadResult postUploadResult = new PostUploadResult();
        List<Attachment> attachments = extractAttachments(
                postUploadRequest.getBoardId(),
                postUploadRequest.getPostId(),
                postUploadRequest.getBody());

        VerificationResult verificationResult = verifyPostUploadRequest(postUploadRequest);

        if (!verificationResult.isVerified()) {
            postUploadResult.setErrorMessages(verificationResult.getMessages());
            postUploadResult.setResultMessage(postErrorMessageSource.getString(POST_UPLOAD_FAILURE));
            return postUploadResult;
        }

        try {
            if (attachments.size() > 0) {
                uploadAttachments(attachments);
                uploadThumbnail(attachments.get(0));
            }

            if (postMapper.uploadPost(postUploadRequest)) {
                throw new SQLException();
            }
        } catch (Exception e) {
            log.info("{}", e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
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
