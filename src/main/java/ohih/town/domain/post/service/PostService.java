package ohih.town.domain.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.ValidationResult;
import ohih.town.constants.*;
import ohih.town.domain.SimpleResponse;
import ohih.town.domain.post.dto.*;
import ohih.town.domain.post.mapper.PostMapper;
import ohih.town.domain.user.dto.UserInfo;
import ohih.town.exception.FileSizeExceedLimitException;
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

    public List<Attachment> extractAttachmentsFromPost(Long boardId, List<String> extractedImages) {
        List<Attachment> attachments = new ArrayList<>();
        String date = Utilities.getDate(DATE_FORMAT_YYYY_MM_DD);

        for (String extractedImage : extractedImages) {
            String fileName = Utilities.createCode(UUID_FULL_INDEX);
            String extension = Utilities.extractExtension(extractedImage);
            Path directory = Paths.get(ConfigurationResourceBundle.FILE_PATHS.getString(ATTACHMENT_PATHS),
                    boardId.toString(),
                    date).resolve(fileName + "." + extension);

            Attachment attachment = new Attachment();
            attachment.setImageData(extractedImage.substring(extractedImage.indexOf(",") + 1));
            attachment.setFileName(fileName);
            attachment.setExtension(extension);
            attachment.setDirectory(directory.toString());

            attachment.setSize(Utilities.getBytesLength(attachment.getImageData()));

            attachments.add(attachment);
        }

        return attachments;
    }


    public boolean checkValidations(PostEditResult postEditResult,
                                    PostAuthorInfo postAuthorInfo,
                                    PostContentInfo postContentInfo) {
        boolean isValid = true;

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

        List<Map<String, Boolean>> fieldValidations = new ArrayList<>();
        List<Map<String, String>> errorMessages = new ArrayList<>();

        for (ValidationResult validationResult : validationResults) {
            fieldValidations.add(validationResult.getFieldValidation());
            errorMessages.add(validationResult.getMessage());

            if (!validationResult.getIsValid()) {
                postEditResult.setFieldValidations(fieldValidations);
                postEditResult.setErrorMessages(errorMessages);
                isValid = false;
            }
        }
        return isValid;
    }


    public void setPostAuthor(PostAuthorInfo postAuthorInfo, UserInfo userInfo, String ip) {
        postAuthorInfo.setIp(ip);
        if (userInfo == null) {
            postAuthorInfo.setUserType(UserConst.USER_TYPE_GUEST);
        } else {
            postAuthorInfo.setUserId(userInfo.getUserId());
            postAuthorInfo.setUserType(userInfo.getUserType());
            postAuthorInfo.setAuthor(userInfo.getUsername());
            postAuthorInfo.setPassword("");
        }
    }

    public void setPostContent(PostContentInfo postContentInfo, List<Attachment> attachments) {
        for (Attachment attachment : attachments) {
            postContentInfo.setBody(replaceAttachmentsInBody(
                    postContentInfo.getBody(), attachment, BASE_64));
        }
    }


    public void uploadAttachments(List<Attachment> attachments, Long postId)
            throws IOException, SQLException, NotAllowedExtensionException, FileSizeExceedLimitException {
        for (Attachment attachment : attachments) {
            Utilities.isAllowedExtension(attachment.getExtension());
            Utilities.isFileSizeExceedLimit(attachment.getSize());

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

    private List<Attachment> getAttachmentsByPostId(Long postId) {
        return postMapper.getAttachmentByPostId(postId);
    }

    public void deleteAttachmentsByPostId(Long postId) {
        List<Attachment> attachments = getAttachmentsByPostId(postId);

        for (Attachment attachment : attachments) {
            File file = new File(attachment.getDirectory());

            if (!file.delete()) {
                log.info("File delete failed. {}", attachment.getDirectory());
            }

            if (!postMapper.deleteAttachmentsByFileName(attachment.getFileName())) {
                log.info("File delete failed. {}", attachment.getFileName());

            }
        }
    }


    private void setThumbnail(String fileName, Long postId, String directory) throws SQLException {
        if (!postMapper.setThumbnail(new Thumbnail(fileName, postId, directory))) {
            throw new SQLException();
        }
    }

    private void deleteThumbnail(Long postId) {
        postMapper.deleteThumbnailByPostId(postId);
    }


    public SimpleResponse checkPostAccessPermission(UserInfo userInfo, String password, Long postId) {
        PostAccessInfo postAccessInfo = postMapper.getPostAccessInfoByPostId(postId);
        SimpleResponse simpleResponse = new SimpleResponse();
        simpleResponse.setSuccess(false);

        if (postAccessInfo == null) {
            return simpleResponse;
        }

        if (postAccessInfo.getUserType().equals(UserConst.USER_TYPE_GUEST)) {
            if (Objects.equals(postAccessInfo.getPassword(), (password))) {
                simpleResponse.setSuccess(true);
            } else {
                simpleResponse.setMessage(POST_ERROR_MESSAGES.getString(POST_PERMISSION_ERROR));
            }
        } else if (postAccessInfo.getUserType().equals(UserConst.USER_TYPE_MEMBER)) {
            if (userInfo == null
                    || postAccessInfo.getUserId().equals(userInfo.getUserId())) {
                simpleResponse.setMessage(POST_ERROR_MESSAGES.getString(POST_PERMISSION_ERROR));
            } else {
                simpleResponse.setSuccess(true);
            }
        }


        if (simpleResponse.getSuccess()) {
            simpleResponse.setMessage(SUCCESS_MESSAGES.getString(POST_PERMISSION_SUCCESS));
        }

        return simpleResponse;
    }

    public PostUpdateInfo getPostUpdateInfoByPostId(Long postId) {
        return postMapper.getPostUpdateInfoByPostId(postId);
    }


    public PostDetails getPostDetailsByPostId(Long postId) {
        return postMapper.getPostDetailsByPostId(postId);
    }

    @Transactional
    public void uploadPost(List<Attachment> attachments, PostAuthorInfo postAuthorInfo, PostContentInfo postContentInfo)
            throws SQLException, IOException, NotAllowedExtensionException, FileSizeExceedLimitException {
        PostUploadRequest postUploadRequest = new PostUploadRequest(postAuthorInfo, postContentInfo);

        if (!postMapper.uploadPost(postUploadRequest)) {
            throw new SQLException();
        }

        uploadAttachments(attachments, postUploadRequest.getPostId());

        if (attachments.size() > 0) {
            setThumbnail(attachments.get(0).getFileName(), attachments.get(0).getPostId(), attachments.get(0).getDirectory());
        }
    }

    /*
     * uploadPostExceptionHandler
     *  uploadPost
     *      postMapper.uploadPost
     *      uploadAttachments - postMapper.uploadAttachment
     *      setThumbnail
     * */
    public void uploadPostExceptionHandler(PostEditResult postEditResult,
                                           List<Attachment> attachments,
                                           PostAuthorInfo postAuthorInfo, PostContentInfo postContentInfo) {
        try {
            uploadPost(attachments, postAuthorInfo, postContentInfo);
            postEditResult.setSuccess(true);
            postEditResult.setSuccessMessage(SUCCESS_MESSAGES.getString(POST_UPLOAD_SUCCESS));
            postEditResult.setRedirectUrl(URLConst.GET_BOARD_PAGE);
        } catch (IOException e) {
            Map<String, String> errorMessage = Collections.singletonMap(
                    POST_UPLOAD_IO_EXCEPTION, POST_ERROR_MESSAGES.getString(POST_UPLOAD_IO_EXCEPTION));
            postEditResult.setErrorMessages(Collections.singletonList(errorMessage));
        } catch (SQLException e) {
            Map<String, String> errorMessage = Collections.singletonMap(
                    POST_UPLOAD_SQL_EXCEPTION, POST_ERROR_MESSAGES.getString(POST_UPLOAD_SQL_EXCEPTION));
            postEditResult.setErrorMessages(Collections.singletonList(errorMessage));
        } catch (NotAllowedExtensionException e) {
            Map<String, String> errorMessage = Collections.singletonMap(
                    UPLOAD_ATTACHMENT_EXTENSION_ERROR, POST_ERROR_MESSAGES.getString(UPLOAD_ATTACHMENT_EXTENSION_ERROR));
            postEditResult.setErrorMessages(Collections.singletonList(errorMessage));
        } catch (FileSizeExceedLimitException e) {
            Map<String, String> errorMessage = Collections.singletonMap(
                    UPLOAD_ATTACHMENT_SIZE_ERROR, POST_ERROR_MESSAGES.getString(UPLOAD_ATTACHMENT_SIZE_ERROR));
            postEditResult.setErrorMessages(Collections.singletonList(errorMessage));
        }
    }

    @Transactional
    public void updatePost(List<Attachment> attachments, PostAuthorInfo postAuthorInfo, PostContentInfo postContentInfo)
            throws SQLException, IOException {
        PostUploadRequest postUploadRequest = new PostUploadRequest(postAuthorInfo, postContentInfo);

        deleteThumbnail(postContentInfo.getPostId());
        deleteAttachmentsByPostId(postContentInfo.getPostId());


        if (postMapper.updatePost(postUploadRequest) != 1) {
            throw new SQLException();
        }

        uploadAttachments(attachments, postUploadRequest.getPostId());

        if (attachments.size() > 0) {
            setThumbnail(attachments.get(0).getFileName(), attachments.get(0).getPostId(), attachments.get(0).getDirectory());
        }
    }

    public void updatePostExceptionHandler(PostEditResult postEditResult,
                                           List<Attachment> attachments,
                                           PostAuthorInfo postAuthorInfo, PostContentInfo postContentInfo) {
        try {
            updatePost(attachments, postAuthorInfo, postContentInfo);
            postEditResult.setSuccess(true);
            postEditResult.setSuccessMessage(SUCCESS_MESSAGES.getString(POST_UPDATE_SUCCESS));
            postEditResult.setRedirectUrl("/post/" + postContentInfo.getPostId());
        } catch (IOException e) {
            Map<String, String> errorMessage = Collections.singletonMap(
                    POST_UPLOAD_IO_EXCEPTION, POST_ERROR_MESSAGES.getString(POST_UPDATE_IO_EXCEPTION));
            postEditResult.setErrorMessages(Collections.singletonList(errorMessage));
        } catch (SQLException e) {
            Map<String, String> errorMessage = Collections.singletonMap(
                    POST_UPLOAD_SQL_EXCEPTION, POST_ERROR_MESSAGES.getString(POST_UPDATE_SQL_EXCEPTION));
            postEditResult.setErrorMessages(Collections.singletonList(errorMessage));
        } catch (NotAllowedExtensionException e) {
            Map<String, String> errorMessage = Collections.singletonMap(
                    UPLOAD_ATTACHMENT_EXTENSION_ERROR, POST_ERROR_MESSAGES.getString(UPLOAD_ATTACHMENT_EXTENSION_ERROR));
            postEditResult.setErrorMessages(Collections.singletonList(errorMessage));
        } catch (FileSizeExceedLimitException e) {
            Map<String, String> errorMessage = Collections.singletonMap(
                    UPLOAD_ATTACHMENT_SIZE_ERROR, POST_ERROR_MESSAGES.getString(UPLOAD_ATTACHMENT_SIZE_ERROR));
            postEditResult.setErrorMessages(Collections.singletonList(errorMessage));
        }
    }

}
