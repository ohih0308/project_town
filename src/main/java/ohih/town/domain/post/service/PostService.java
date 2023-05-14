package ohih.town.domain.post.service;

import ohih.town.domain.AccessPermissionCheckResult;
import ohih.town.domain.VerificationResult;
import ohih.town.domain.post.dto.*;
import ohih.town.domain.user.dto.UserInfo;

import java.util.List;

public interface PostService {

    AccessPermissionCheckResult checkAccessPermission(Long userId, Long postId, String password);

    VerificationResult verifyPostUploadRequest(PostUploadRequest postUploadRequest);

    List<Attachment> extractAttachments(Long boardId, String body);

    void setPostContent(PostContentInfo postContentInfo, List<Attachment> attachments);

    boolean uploadAttachments_prj(List<Attachment> attachments, Long postId);

    boolean uploadAttachments_db(List<Attachment> attachments, Long postId);

    boolean updateAttachments_db(List<Attachment> attachments, Long postId);

    List<Attachment> getAttachments(Long postId);

    boolean deleteAttachments_prj(Long postId);

    boolean uploadThumbnail(Attachment attachment);

    boolean updateThumbnail(Attachment attachment);
    void deleteThumbnail(Long postId);

    boolean checkAccessPermission(UserInfo userInfo, String password, Long postId);

    PostDetails getPostDetails(Long postId);

    PostUploadResult uploadPost(PostUploadRequest postUploadRequest, List<Attachment> attachments);

    PostUploadResult updatePost(PostUploadRequest postUploadRequest, List<Attachment> attachments);

    void deletePost(Long postId);
}
