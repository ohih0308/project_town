package ohih.town.domain.post.service;

import ohih.town.domain.AccessPermissionCheckResult;
import ohih.town.domain.VerificationResult;
import ohih.town.domain.post.dto.SimplePost;
import ohih.town.domain.post.dto.*;
import ohih.town.utilities.Paging;
import ohih.town.utilities.Search;

import java.util.List;

public interface PostService {

    //    void setPostContent(PostContentInfo postContentInfo, List<Attachment> attachments);
    //    boolean updateAttachments_db(List<Attachment> attachments, Long postId);
    //    List<Attachment> getAttachments(Long postId);
    //    boolean checkAccessPermission(UserInfo userInfo, String password, Long postId);

    AccessPermissionCheckResult checkAccessPermission(Long userId, Long postId, String password);

    VerificationResult verifyPostUploadRequest(PostUploadRequest postUploadRequest);

    List<Attachment> extractAttachments(Long boardId, String body);


    PostContent getPostContent(Long postId);

    PostDetails getPostDetails(Long postId);

    Long countPosts(Long boardId, Search search);

    List<SimplePost> getPosts(Long boardId, Paging paging, Search search);

    Long countMyPosts(Long userId, Search search);

    List<SimplePost> getMyPosts(Long userId, Paging paging, Search search);


    boolean uploadAttachments_prj(List<Attachment> attachments, Long postId);

    boolean uploadAttachments_db(List<Attachment> attachments, Long postId);

    boolean uploadThumbnail(Attachment attachment);

    PostResult uploadPost(PostUploadRequest postUploadRequest, List<Attachment> attachments);


    boolean updateThumbnail(Attachment attachment);

    PostResult updatePost(Long accessPermittedPostId,
                          PostUploadRequest postUploadRequest, List<Attachment> attachments);


    boolean deleteAttachments_prj(Long postId);

    boolean deleteAttachments_db(Long postId);

    boolean deleteThumbnail(Long postId);

    PostResult deletePost(Long accessPermittedPostId, Long postId);


    boolean deleteComments(Long postId);


}
