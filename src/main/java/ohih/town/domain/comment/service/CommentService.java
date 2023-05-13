package ohih.town.domain.comment.service;

import ohih.town.domain.AccessPermissionCheckResult;
import ohih.town.domain.VerificationResult;
import ohih.town.domain.comment.dto.CommentContentInfo;
import ohih.town.domain.comment.dto.CommentDeleteResult;
import ohih.town.domain.comment.dto.CommentUploadRequest;
import ohih.town.domain.comment.dto.CommentUploadResult;
import ohih.town.domain.common.dto.AuthorInfo;

import java.util.ResourceBundle;

import static ohih.town.constants.ResourceBundleConst.COMMENT_ERROR_MESSAGES;
import static ohih.town.constants.ResourceBundleConst.SUCCESS_MESSAGES;

public interface CommentService {
    /*
     * class list:
     *   AuthorInfo, CommentContentInfo
     * */


    VerificationResult verifyCommentUploadRequest(CommentUploadRequest commentUploadRequest);

    CommentUploadResult uploadComment(CommentUploadRequest commentUploadRequest);

    AccessPermissionCheckResult checkAccessPermission(Long userId, Long commentId, String password);

    CommentDeleteResult deleteComment(Long accessPermittedCommentId, Long commentId);

    void deleteCommentsByPostId(Long postId);


    Integer getTotalCountsByPostId(Long postId);


}
