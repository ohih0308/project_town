package ohih.town.domain.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService123 {

    private final CommonService commonService;
    private final CommentService commentService;

    private final PostMapper postMapper;


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


//    public boolean checkValidations(ActionResult actionResult,
//                                    AuthorInfo authorInfo,
//                                    PostContentInfo postContentInfo) {
//        boolean isValid = true;
//
//        ValidationResult[] validationResults = {
//                commonService.checkValidation(ValidationPatterns.USERNAME,
//                        USER_ERROR_MESSAGES, SUCCESS_MESSAGES,
//                        USER_USERNAME_INVALID, USER_USERNAME_VALID,
//                        PostConst.AUTHOR, authorInfo.getAuthor()),
//                commonService.checkValidation(ValidationPatterns.PASSWORD,
//                        USER_ERROR_MESSAGES, SUCCESS_MESSAGES,
//                        USER_PASSWORD_INVALID, USER_PASSWORD_VALID,
//                        PostConst.PASSWORD, authorInfo.getPassword()),
//                commonService.checkValidation(ValidationPatterns.SUBJECT,
//                        POST_ERROR_MESSAGES, SUCCESS_MESSAGES,
//                        POST_SUBJECT_INVALID, POST_SUBJECT_VALID,
//                        PostConst.SUBJECT, postContentInfo.getSubject()),
//                commonService.checkValidation(ValidationPatterns.BODY,
//                        POST_ERROR_MESSAGES, SUCCESS_MESSAGES,
//                        POST_BODY_INVALID, POST_BODY_VALID,
//                        PostConst.BODY, postContentInfo.getBody())
//        };
//
//        List<Map<String, Boolean>> fieldValidations = new ArrayList<>();
//        List<Map<String, String>> errorMessages = new ArrayList<>();
//
//        for (ValidationResult validationResult : validationResults) {
//            fieldValidations.add(validationResult.getFieldValidation());
//            errorMessages.add(validationResult.getMessage());
//
//            if (!validationResult.getIsValid()) {
//                actionResult.setFieldValidations(fieldValidations);
//                actionResult.setErrorMessages(errorMessages);
//                isValid = false;
//            }
//        }
//        return isValid;
//    }


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

    public void deleteAttachmentsByPostId(Long postId) throws PartialDeleteException {
        List<Attachment> attachments = getAttachmentsByPostId(postId);

        for (Attachment attachment : attachments) {
            File file = new File(attachment.getDirectory());

            if (!file.delete()) {
                log.info("File delete failed. {}", attachment.getDirectory());
            }

            if (!postMapper.deleteAttachmentsByFileName(attachment.getFileName())) {
                log.info("File delete failed. {}", attachment.getFileName());
                throw new PartialDeleteException();
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


    public PostAccessInfo getPostAccessInfoByPostId(Long postId) {
        return postMapper.getPostAccessInfoByPostId(postId);
    }

    public SimpleResponse checkPostAccessPermission(UserInfo userInfo, String password, Long postId) {
        SimpleResponse simpleResponse = new SimpleResponse();
        PostAccessInfo postAccessInfo = getPostAccessInfoByPostId(postId);
        simpleResponse.setSuccess(false);

        if (postAccessInfo == null) {
            simpleResponse.setMessage(POST_ERROR_MESSAGES.getString(POST_EXISTENCE_ERROR));
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

    public PostUpdateRequest getPostUpdateInfoByPostId(Long postId) {
        return postMapper.getPostUpdateInfoByPostId(postId);
    }


    public PostDetails getPostDetailsByPostId(Long postId) {
        return postMapper.getPostDetailsByPostId(postId);
    }

    @Transactional
    public void uploadPost(List<Attachment> attachments, AuthorInfo authorInfo, PostContentInfo postContentInfo)
            throws SQLException, IOException, NotAllowedExtensionException, FileSizeExceedLimitException {
        PostUploadRequest postUploadRequest = new PostUploadRequest(authorInfo, postContentInfo);

        if (!postMapper.uploadPost(postUploadRequest)) {
            throw new SQLException();
        }

        uploadAttachments(attachments, postUploadRequest.getPostId());

        if (attachments.size() > 0) {
            setThumbnail(attachments.get(0).getFileName(), attachments.get(0).getPostId(), attachments.get(0).getDirectory());
        }
    }

    @Transactional
    public void updatePost(List<Attachment> attachments, AuthorInfo authorInfo, PostContentInfo postContentInfo)
            throws SQLException, IOException, PartialDeleteException {
        PostUploadRequest postUploadRequest = new PostUploadRequest(authorInfo, postContentInfo);

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

    @Transactional
    public void deletePost(Long postId) throws PartialDeleteException, SQLException {
        deleteThumbnail(postId);

        List<Attachment> attachments = getAttachmentsByPostId(postId);

        for (Attachment attachment : attachments) {
            File file = new File(attachment.getDirectory());
            if (!file.delete()) {
                throw new PartialDeleteException();
            }
        }

        deleteAttachmentsByPostId(postId);
        commentService.deleteCommentsByPostId(postId);

        if (postMapper.deletePost(postId) != 1) {
            throw new SQLException();
        }
    }

    /*
     * uploadPostExceptionHandler
     *  uploadPost
     *      postMapper.uploadPost
     *      uploadAttachments - postMapper.uploadAttachment
     *      setThumbnail
     * */
    public void uploadPostExceptionHandler(ActionResult actionResult,
                                           List<Attachment> attachments,
                                           AuthorInfo authorInfo, PostContentInfo postContentInfo) {
        try {
            uploadPost(attachments, authorInfo, postContentInfo);
            actionResult.setSuccess(true);
            actionResult.setSuccessMessage(SUCCESS_MESSAGES.getString(POST_UPLOAD_SUCCESS));
            actionResult.setRedirectUrl(URLConst.GET_BOARD_PAGE);
        } catch (IOException e) {
            Map<String, String> errorMessage = Collections.singletonMap(
                    POST_UPLOAD_IO_EXCEPTION, POST_ERROR_MESSAGES.getString(POST_UPLOAD_IO_EXCEPTION));
            actionResult.setErrorMessages(Collections.singletonList(errorMessage));
        } catch (SQLException e) {
            Map<String, String> errorMessage = Collections.singletonMap(
                    POST_UPLOAD_SQL_EXCEPTION, POST_ERROR_MESSAGES.getString(POST_UPLOAD_SQL_EXCEPTION));
            actionResult.setErrorMessages(Collections.singletonList(errorMessage));
        } catch (NotAllowedExtensionException e) {
            Map<String, String> errorMessage = Collections.singletonMap(
                    UPLOAD_ATTACHMENT_EXTENSION_ERROR, POST_ERROR_MESSAGES.getString(UPLOAD_ATTACHMENT_EXTENSION_ERROR));
            actionResult.setErrorMessages(Collections.singletonList(errorMessage));
        } catch (FileSizeExceedLimitException e) {
            Map<String, String> errorMessage = Collections.singletonMap(
                    UPLOAD_ATTACHMENT_SIZE_ERROR, POST_ERROR_MESSAGES.getString(UPLOAD_ATTACHMENT_SIZE_ERROR));
            actionResult.setErrorMessages(Collections.singletonList(errorMessage));
        }
    }

    public void updatePostExceptionHandler(ActionResult actionResult,
                                           List<Attachment> attachments,
                                           AuthorInfo authorInfo, PostContentInfo postContentInfo) {
        try {
            updatePost(attachments, authorInfo, postContentInfo);
            actionResult.setSuccess(true);
            actionResult.setSuccessMessage(SUCCESS_MESSAGES.getString(POST_UPDATE_SUCCESS));
            actionResult.setRedirectUrl("/post/" + postContentInfo.getPostId());
        } catch (IOException e) {
            Map<String, String> errorMessage = Collections.singletonMap(
                    POST_UPLOAD_IO_EXCEPTION, POST_ERROR_MESSAGES.getString(POST_UPDATE_IO_EXCEPTION));
            actionResult.setErrorMessages(Collections.singletonList(errorMessage));
        } catch (SQLException e) {
            Map<String, String> errorMessage = Collections.singletonMap(
                    POST_UPLOAD_SQL_EXCEPTION, POST_ERROR_MESSAGES.getString(POST_UPDATE_SQL_EXCEPTION));
            actionResult.setErrorMessages(Collections.singletonList(errorMessage));
        } catch (NotAllowedExtensionException e) {
            Map<String, String> errorMessage = Collections.singletonMap(
                    UPLOAD_ATTACHMENT_EXTENSION_ERROR, POST_ERROR_MESSAGES.getString(UPLOAD_ATTACHMENT_EXTENSION_ERROR));
            actionResult.setErrorMessages(Collections.singletonList(errorMessage));
        } catch (FileSizeExceedLimitException e) {
            Map<String, String> errorMessage = Collections.singletonMap(
                    UPLOAD_ATTACHMENT_SIZE_ERROR, POST_ERROR_MESSAGES.getString(UPLOAD_ATTACHMENT_SIZE_ERROR));
            actionResult.setErrorMessages(Collections.singletonList(errorMessage));
        } catch (PartialDeleteException e) {
            Map<String, String> errorMessage = Collections.singletonMap(
                    POST_DELETE_PARTIAL_EXCEPTION, POST_ERROR_MESSAGES.getString(POST_DELETE_PARTIAL_EXCEPTION));
            actionResult.setErrorMessages(Collections.singletonList(errorMessage));
        }
    }

    public void deletePostExceptionHandler(ActionResult actionResult,
                                           Long postId, String boardName) {
        try {
            deletePost(postId);
            actionResult.setSuccess(true);
            actionResult.setSuccessMessage(SUCCESS_MESSAGES.getString(POST_DELETE_SUCCESS));
            actionResult.setRedirectUrl("/board/" + boardName);
        } catch (PartialDeleteException e) {
            Map<String, String> errorMessage = Collections.singletonMap(
                    POST_DELETE_PARTIAL_EXCEPTION, POST_ERROR_MESSAGES.getString(POST_DELETE_PARTIAL_EXCEPTION));
            actionResult.setErrorMessages(Collections.singletonList(errorMessage));
        } catch (SQLException e) {
            Map<String, String> errorMessage = Collections.singletonMap(
                    POST_DELETE_ERROR, POST_ERROR_MESSAGES.getString(POST_DELETE_ERROR));
            actionResult.setErrorMessages(Collections.singletonList(errorMessage));
        }
    }
}
