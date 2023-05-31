package ohih.town.domain.guestbook.service;

import ohih.town.domain.AccessPermissionCheckResult;
import ohih.town.domain.VerificationResult;
import ohih.town.domain.comment.dto.CommentResult;
import ohih.town.AuthorInfo;
import ohih.town.domain.guestbook.dto.ContentInfo;
import ohih.town.domain.guestbook.dto.Guestbook;
import ohih.town.domain.guestbook.dto.GuestbookResult;
import ohih.town.domain.post.dto.PostResult;
import ohih.town.utilities.Paging;
import ohih.town.utilities.Search;

import java.util.List;


public interface GuestbookService {

    AccessPermissionCheckResult checkPostAccessPermission(Long userId, Long postId, String password);

    AccessPermissionCheckResult checkCommentAccessPermission(Long userId, Long commentId, String password);

    GuestbookResult checkGuestbookConfigs(Long ownerId, AuthorInfo authorInfo);

    VerificationResult verifyPostUploadRequest(AuthorInfo authorInfo, ContentInfo contentInfo);

    Long countPosts(Long ownerId, Search search);

    List<Guestbook> getPosts(Long ownerId, Long userId, Paging paging);

    Long countComments(Long postId);

    boolean isGuestbookOwner(Long postId, Long userId);

    List<Guestbook> getComments(Long postId, Long userId, Paging paging);


    PostResult uploadPost(AuthorInfo authorInfo, ContentInfo contentInfo);

    CommentResult uploadComment(AuthorInfo authorInfo, ContentInfo contentInfo);

    PostResult deletePost(Long accessPermittedPostId, Long postId);

    CommentResult deleteComment(Long accessPermittedCommentId, Long commentId);
}
