package ohih.town.domain.guestbook.service;

import ohih.town.domain.AccessPermissionCheckResult;
import ohih.town.domain.VerificationResult;
import ohih.town.domain.common.dto.AuthorInfo;
import ohih.town.domain.guestbook.dto.ContentInfo;
import ohih.town.domain.guestbook.dto.GuestbookResult;
import ohih.town.domain.post.dto.PostResult;


public interface GuestbookService {

    AccessPermissionCheckResult checkAccessPermission(Long userId, Long postId, String password);

    GuestbookResult checkGuestbookConfigs(Long ownerId, AuthorInfo authorInfo);

    VerificationResult verifyPostUploadRequest(AuthorInfo authorInfo, ContentInfo contentInfo);

    PostResult uploadPost(AuthorInfo authorInfo, ContentInfo contentInfo);

    PostResult deletePost(Long accessPermittedPostId, Long postId);
}
