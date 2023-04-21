package ohih.town.domain.forum.service;

import ohih.town.domain.forum.dto.Forum;
import ohih.town.utilities.Search;

import java.util.List;

public interface ForumService {

    List<Forum> getAllForums();

    Long getBoardPostTotalCount(String boardName, Search search);

    String getBoardNameByPostId(Long postId);
}
