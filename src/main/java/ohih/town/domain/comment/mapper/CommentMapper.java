package ohih.town.domain.comment.mapper;

import ohih.town.domain.comment.dto.CommentAccessInfo;
import ohih.town.domain.comment.dto.CommentUploadRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    boolean isPostIdExists(Long postId);

    boolean uploadComment(CommentUploadRequest commentUploadRequest);

    CommentAccessInfo getCommentAccessInfoByCommentId(Long commentId);

    boolean deleteComment(Long commentId);

    Integer getTotalCommentCountByPostId(Long postId);

    List<Long> getCommentIdsByPostId(Long postId);
}
