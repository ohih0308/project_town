package ohih.town.domain.forum.service;

import lombok.RequiredArgsConstructor;
import ohih.town.constants.ForumConst;
import ohih.town.domain.forum.dto.BoardPost;
import ohih.town.domain.forum.dto.Forum;
import ohih.town.domain.forum.mapper.ForumMapper;
import ohih.town.utilities.Paging;
import ohih.town.utilities.Search;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ohih.town.constants.ForumConst.BOARD_NAME;
import static ohih.town.constants.UtilityConst.PAGING;
import static ohih.town.constants.UtilityConst.SEARCH;

@Service
@RequiredArgsConstructor
public class ForumService {
    private final ForumMapper forumMapper;


    public List<Forum> getAllForums() {
        return forumMapper.getAllForums();
    }

    public Long getBoardPostTotalCount(String boardName, Search search) {
        Map<String, Object> map = new HashMap();
        map.put(BOARD_NAME, boardName);
        map.put(SEARCH, search);

        return forumMapper.getBoardPostTotalCount(map);
    }

    public List<BoardPost> getBoardPosts(Paging paging, Search search, String boardName) {
        Map map = new HashMap();
        map.put(PAGING, paging);
        map.put(SEARCH, search);
        map.put(BOARD_NAME, boardName);

        return forumMapper.getBoardPosts(map);
    }


    public String getBoardNameByPostId(Long postId) {
        return forumMapper.getBoardNameByPostId(postId);
    }
}
