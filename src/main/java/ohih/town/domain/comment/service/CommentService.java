package ohih.town.domain.comment.service;

import ohih.town.domain.comment.dto.CommentContentInfo;
import ohih.town.domain.common.dto.AuthorInfo;

public interface CommentService {

    /*
     * class list:
     *   AuthorInfo, CommentContentInfo
     * */

    void uploadComment(AuthorInfo authorInfo, CommentContentInfo commentContentInfo);

    void deleteComment(Long commentId);

    void deleteCommentsByPostId(Long postId);


    boolean checkAccessPermission(Long userId, String password, Long commentId);

    Integer getTotalCountsByPostId(Long postId);


}
