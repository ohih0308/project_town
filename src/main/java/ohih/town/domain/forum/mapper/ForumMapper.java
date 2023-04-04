package ohih.town.domain.forum.mapper;

import ohih.town.domain.forum.dto.BoardPost;
import ohih.town.domain.forum.dto.Forum;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ForumMapper {
    List<Forum> getAllForums();

    Long getBoardPostTotalCount(Map map);

    List<BoardPost> getBoardPosts(Map map);

    String getBoardNameByPostId(Long postId);
}
