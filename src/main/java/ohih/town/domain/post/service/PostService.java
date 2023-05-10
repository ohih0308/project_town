package ohih.town.domain.post.service;

import ohih.town.domain.VerificationResult;
import ohih.town.domain.common.dto.AuthorInfo;
import ohih.town.domain.post.dto.*;
import ohih.town.domain.user.dto.UserInfo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import static ohih.town.constants.ResourceBundleConst.POST_ERROR_MESSAGES;
import static ohih.town.constants.ResourceBundleConst.SUCCESS_MESSAGES;

public interface PostService {

    ResourceBundle postErrorMessageSource = POST_ERROR_MESSAGES;
    ResourceBundle successMessageSource = SUCCESS_MESSAGES;

    VerificationResult verifyPostUploadRequest(PostUploadRequest postUploadRequest);

    List<Attachment> extractAttachments(Long boardId, Long postId, String body);

    void setPostContent(PostContentInfo postContentInfo, List<Attachment> attachments);

    void uploadAttachments(List<Attachment> attachments) throws IOException, SQLException;

    List<Attachment> getAttachments(Long postId);

    void deleteAttachments(Long postId);

    boolean uploadThumbnail(Attachment attachment);

    void deleteThumbnail(Long postId);

    boolean checkAccessPermission(UserInfo userInfo, String password, Long postId);

    PostDetails getPostDetails(Long postId);

    PostUploadResult uploadPost(PostUploadRequest postUploadRequest);

    void updatePost(List<Attachment> attachments, AuthorInfo authorInfo, PostContentInfo postContentInfo);

    void deletePost(Long postId);
}
