package ohih.town.domain.comment.service;

import ohih.town.domain.AccessPermissionCheckResult;
import ohih.town.domain.VerificationResult;
import ohih.town.domain.comment.dto.CommentUploadRequest;
import ohih.town.domain.comment.dto.CommentResult;

public interface CommentService {
    /*
     * class list:
     *   AuthorInfo, CommentContentInfo
     * */


    VerificationResult verifyCommentUploadRequest(CommentUploadRequest commentUploadRequest);

    CommentResult uploadComment(CommentUploadRequest commentUploadRequest);

    AccessPermissionCheckResult checkAccessPermission(Long userId, Long commentId, String password);

    CommentResult deleteComment(Long accessPermittedCommentId, Long commentId);

}
