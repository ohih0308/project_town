package ohih.town.domain.forum.service;

import lombok.RequiredArgsConstructor;
import ohih.town.constants.ForumConst;
import ohih.town.domain.forum.dto.Forum;
import ohih.town.domain.forum.mapper.ForumMapper;
import ohih.town.utilities.Search;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ohih.town.constants.UtilityConst.SEARCH;

@Service
@RequiredArgsConstructor
public class ForumService {
    private final ForumMapper forumMapper;


    public List<Forum> getAllForums() {
        return forumMapper.getAllForums();
    }

    public Long getBoardPostTotalCount(String name, Search search) {
        Map<String, Object> map = new HashMap();
        map.put(ForumConst.BOARD_NAME, name);
        map.put(SEARCH, search);

        return forumMapper.getBoardPostTotalCount(map);
    }
}
