package ohih.town.domain.post.service;

import ohih.town.domain.common.dto.AuthorInfo;
import ohih.town.domain.post.dto.Attachment;
import ohih.town.domain.post.dto.PostAccessInfo;
import ohih.town.domain.post.dto.PostContentInfo;
import ohih.town.domain.post.dto.PostDetails;
import ohih.town.domain.user.dto.UserInfo;

import java.util.List;

public interface PostService {

    List<Attachment> extractBase64DataFromString(String body);

    void setPostContent(PostContentInfo postContentInfo, List<Attachment> attachments);

    void uploadAttachments(List<Attachment> attachments);

    List<Attachment> getAttachmentsByPostId(Long postId);

    void deleteAttachmentsByPostId(Long postId);

    void uploadThumbnail(Long postId, String fileName, String directory);

    void deleteThumbnailByPostId(Long postId);

    boolean checkAccessPermission(UserInfo userInfo, String password, Long postId);

    PostDetails getPostDetailsByPostId(Long postId);

    void uploadPost(List<Attachment> attachments, AuthorInfo authorInfo, PostContentInfo postContentInfo);

    void updatePost(List<Attachment> attachments, AuthorInfo authorInfo, PostContentInfo postContentInfo);

    void deletePostByPostId(Long postId);
}
