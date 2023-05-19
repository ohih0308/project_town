package ohih.town.domain.comment.mapper;

import ohih.town.domain.AccessInfo;
import ohih.town.domain.comment.dto.Comment;
import ohih.town.domain.comment.dto.CommentUploadRequest;
import ohih.town.utilities.Paging;
import ohih.town.utilities.Search;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommentMapper {
    boolean isPostIdExists(Long postId);

    AccessInfo getAccessInfo(Long commentId);

    Long countComments(Long postId);

    List<Comment> getComments(Map<String, Object> map);

    Long countMyComments(Map<String, Object> map);

    List<Comment> getMyComments(Map<String, Object> map);


    boolean uploadComment(CommentUploadRequest commentUploadRequest);

    boolean deleteComment(Long commentId);

    Integer getTotalCommentCountByPostId(Long postId);

    List<Long> getCommentIdsByPostId(Long postId);
}
