package ohih.town.domain.comment.service;

import ohih.town.domain.AccessPermissionCheckResult;
import ohih.town.domain.VerificationResult;
import ohih.town.domain.comment.dto.Comment;
import ohih.town.domain.comment.dto.CommentUploadRequest;
import ohih.town.domain.comment.dto.CommentResult;
import ohih.town.domain.post.dto.SimplePost;
import ohih.town.utilities.Paging;
import ohih.town.utilities.Search;

import java.util.List;

public interface CommentService {


    VerificationResult verifyCommentUploadRequest(CommentUploadRequest commentUploadRequest);

    Long countComments(Long postId);

    List<Comment> getComments(Long postId, Paging paging);

    Long countMyComments(Long userId, Search search);

    List<Comment> getMyComments(Long userId, Paging paging, Search search);


    CommentResult uploadComment(CommentUploadRequest commentUploadRequest);

    AccessPermissionCheckResult checkAccessPermission(Long userId, Long commentId, String password);

    CommentResult deleteComment(Long accessPermittedCommentId, Long commentId);

}
