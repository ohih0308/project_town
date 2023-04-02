package ohih.town.domain.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.ValidationResult;
import ohih.town.constants.*;
import ohih.town.domain.post.dto.*;
import ohih.town.domain.post.mapper.PostMapper;
import ohih.town.domain.user.dto.UserInfo;
import ohih.town.exception.NotAllowedExtensionException;
import ohih.town.utilities.Utilities;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

import static ohih.town.constants.ConfigurationConst.ATTACHMENT_PATHS;
import static ohih.town.constants.DateFormat.DATE_FORMAT_YYYY_MM_DD;
import static ohih.town.constants.EncodeTypeConst.BASE_64;
import static ohih.town.constants.ErrorMessagesResourceBundle.POST_ERROR_MESSAGES;
import static ohih.town.constants.ErrorMessagesResourceBundle.USER_ERROR_MESSAGES;
import static ohih.town.constants.ErrorsConst.*;
import static ohih.town.constants.SuccessConst.*;
import static ohih.town.constants.SuccessMessagesResourceBundle.SUCCESS_MESSAGES;
import static ohih.town.constants.UtilityConst.UUID_FULL_INDEX;
import static ohih.town.utilities.Utilities.replaceAttachmentsInBody;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostMapper postMapper;


    private ValidationResult checkValidation(Pattern pattern,
                                             ResourceBundle errorMessageSource, ResourceBundle successMessageSource,
                                             String invalidMessage,
                                             String validMessage,
                                             String field,
                                             String input) {
        ValidationResult validationResult = new ValidationResult();
        boolean isValid = Utilities.isValidPattern(pattern, input);

        Map<String, String> message = new HashMap<>();

        Map<String, Boolean> fieldValidation = new HashMap<>();

        if (isValid) {
            message.put(field, successMessageSource.getString(validMessage));
            validationResult.setIsValid(true);
            fieldValidation.put(field, true);
        } else {
            message.put(field, errorMessageSource.getString(invalidMessage));
            validationResult.setIsValid(false);
            fieldValidation.put(field, false);
        }

        validationResult.setFieldValidation(fieldValidation);
        validationResult.setMessage(message);

        return validationResult;
    }

    public List<Attachment> getAttachmentsFromPost(List<String> extractedImages, String boardName) {
        List<Attachment> attachments = new ArrayList<>();
        String date = Utilities.getDate(DATE_FORMAT_YYYY_MM_DD);

        for (String extractedImage : extractedImages) {
            String fileName = Utilities.createCode(UUID_FULL_INDEX);
            String extension = Utilities.extractExtension(extractedImage);
            Path directory = Paths.get(ConfigurationResourceBundle.FILE_PATHS.getString(ATTACHMENT_PATHS),
                    boardName,
                    date).resolve(fileName + "." + extension);

            Attachment attachment = new Attachment();
            attachment.setImageDate(extractedImage.substring(extractedImage.indexOf(",") + 1));
            attachment.setFileName(fileName);
            attachment.setExtension(extension);
            attachment.setDirectory(directory.toString());

            attachments.add(attachment);
        }

        return attachments;
    }


    public List<ValidationResult> checkValidations(PostAuthorInfo postAuthorInfo, PostContentInfo postContentInfo) {
        List<ValidationResult> errorMessages = new ArrayList<>();

        ValidationResult[] validationResults = {
                checkValidation(ValidationPatterns.USERNAME,
                        USER_ERROR_MESSAGES, SUCCESS_MESSAGES,
                        USER_USERNAME_INVALID, USER_USERNAME_VALID,
                        PostConst.AUTHOR, postAuthorInfo.getAuthor()),
                checkValidation(ValidationPatterns.PASSWORD,
                        USER_ERROR_MESSAGES, SUCCESS_MESSAGES,
                        USER_PASSWORD_INVALID, USER_PASSWORD_VALID,
                        PostConst.PASSWORD, postAuthorInfo.getPassword()),
                checkValidation(ValidationPatterns.SUBJECT,
                        POST_ERROR_MESSAGES, SUCCESS_MESSAGES,
                        POST_SUBJECT_INVALID, POST_SUBJECT_VALID,
                        PostConst.SUBJECT, postContentInfo.getSubject()),
                checkValidation(ValidationPatterns.BODY,
                        POST_ERROR_MESSAGES, SUCCESS_MESSAGES,
                        POST_BODY_INVALID, POST_BODY_VALID,
                        PostConst.BODY, postContentInfo.getBody())
        };

        errorMessages.addAll(Arrays.asList(validationResults));

        return errorMessages;
    }


    public void setAuthor(PostAuthorInfo postAuthorInfo, UserInfo userInfo, String ip) {
        postAuthorInfo.setIp(ip);

        if (userInfo == null) {
            postAuthorInfo.setUserType(UserConst.USER_TYPE_GUEST);
        } else {
            postAuthorInfo.setUserId(userInfo.getId());
            postAuthorInfo.setUserType(userInfo.getUserType());
            postAuthorInfo.setAuthor(userInfo.getUsername());
        }
    }

    public void setContent(PostContentInfo postContentInfo, List<Attachment> attachments, String boardName) {
//        List<Attachment> attachments =
//                getAttachmentsFromPost(extractAttachmentsFromBody(postUploadContent.getBody()), boardName);
        for (Attachment attachment : attachments) {
            postContentInfo.setBody(replaceAttachmentsInBody(
                    postContentInfo.getBody(), attachment, BASE_64));
        }
    }

    public void uploadAttachments(List<Attachment> attachments, Long postId)
            throws IOException, SQLException, NotAllowedExtensionException {
        for (Attachment attachment : attachments) {
            Utilities.isAllowedExtension(attachment.getExtension());

            attachment.setPostId(postId);

            Path path = Paths.get(attachment.getDirectory());

            File file = path.toFile();
            File directory = new File(path.getParent().toString());

            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    throw new IOException();
                }
            }

            if (!file.exists()) {
                file.createNewFile();
            }

            byte[] imageData = Base64.getDecoder().decode(attachment.getImageDate());

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

    @Transactional
    public void uploadPost(List<Attachment> attachments, PostAuthorInfo postAuthorInfo, PostContentInfo postContentInfo)
            throws SQLException, IOException, NotAllowedExtensionException {
        PostUploadRequest postUploadRequest = new PostUploadRequest(postAuthorInfo, postContentInfo);

        if (!postMapper.uploadPost(postUploadRequest)) {
            throw new SQLException();
        }

        uploadAttachments(attachments, postUploadRequest.getPostId());

        if (attachments.size() > 0) {
            setThumbnail(attachments.get(0).getFileName(), attachments.get(0).getPostId(), attachments.get(0).getDirectory());
        }
    }

    private void setThumbnail(String fileName, Long postId, String directory) throws SQLException {
        if (!postMapper.setThumbnail(new Thumbnail(fileName, postId, directory))) {
            throw new SQLException();
        }
    }


    public boolean isPostAccessGranted(UserInfo userInfo, Long postId, String password) {
        PostAccessInfo postAccessInfo = postMapper.getPostAccessInfoByPostId(postId);
        if (postAccessInfo == null) {
            return false;
        }

        if (userInfo == null) {
            if (postAccessInfo.getUserType() == UserConst.USER_TYPE_GUEST && postAccessInfo.getPassword().equals(password)) {
                return true;
            }
            return false;
        }

        return postAccessInfo.getUserType() == UserConst.USER_TYPE_MEMBER && postAccessInfo.getUserId() == userInfo.getId()
                || userInfo.getUserType() == UserConst.USER_TYPE_ADMIN;
    }

    public PostUpdateInfo getPostUpdateInfoByPostId(Long postId) {
        return postMapper.getPostUpdateInfoByPostId(postId);
    }

}
