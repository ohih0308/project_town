package ohih.town.domain.post.service;

import ohih.town.domain.AccessPermissionCheckResult;
import ohih.town.domain.VerificationResult;
import ohih.town.domain.common.dto.AuthorInfo;
import ohih.town.domain.post.dto.*;
import ohih.town.domain.user.dto.UserInfo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface PostService {

    AccessPermissionCheckResult checkAccessPermission(Long userId, Long postId, String password);

    VerificationResult verifyPostUploadRequest(PostUploadRequest postUploadRequest);

    List<Attachment> extractAttachments(Long boardId, String body);

    void setPostContent(PostContentInfo postContentInfo, List<Attachment> attachments);

    boolean uploadAttachments_prj(List<Attachment> attachments, Long postId) throws IOException, SQLException;

    boolean uploadAttachments_db(List<Attachment> attachments, Long postId) throws IOException, SQLException;

    List<Attachment> getAttachments(Long postId);

    void deleteAttachments(Long postId);

    boolean uploadThumbnail(Attachment attachment) throws SQLException;

    void deleteThumbnail(Long postId);

    boolean checkAccessPermission(UserInfo userInfo, String password, Long postId);

    PostDetails getPostDetails(Long postId);

    PostUploadResult uploadPost(PostUploadRequest postUploadRequest, List<Attachment> attachments);

    void updatePost(List<Attachment> attachments, AuthorInfo authorInfo, PostContentInfo postContentInfo);

    void deletePost(Long postId);
}
