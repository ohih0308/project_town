package ohih.town;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.SessionConst;
import ohih.town.constants.URLConst;
import ohih.town.constants.ViewConst;
import ohih.town.domain.forum.dto.BoardPost;
import ohih.town.domain.forum.dto.Forum;
import ohih.town.domain.forum.service.ForumService;
import ohih.town.domain.user.dto.RegisterRequest;
import ohih.town.domain.user.mapper.UserMapper;
import ohih.town.domain.user.service.UserService;
import ohih.town.session.SessionManager;
import ohih.town.utilities.Paging;
import ohih.town.utilities.Search;
import ohih.town.utilities.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.swing.text.View;
import java.util.List;

import static ohih.town.constants.ForumConst.*;
import static ohih.town.constants.PagingConst.postsPerPage;
import static ohih.town.constants.URLConst.FORUM_SELECTION;
import static ohih.town.constants.URLConst.GET_BOARD_PAGE;
import static ohih.town.constants.UtilityConst.PAGING;
import static ohih.town.constants.UtilityConst.SEARCH;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TownController {

    private final ForumService forumService;

    @Autowired
    ApplicationContext applicationContext;


    @GetMapping("/beans")
    public String beans() {
        String[] beans = applicationContext.getBeanDefinitionNames();

        for (String bean : beans) {
            log.info("bean name = {}", bean);
        }
        return ViewConst.HOME;
    }

    @GetMapping(URLConst.HOME)
    public String home() {
        return ViewConst.HOME;
    }


    @GetMapping(URLConst.REGISTER_URL)
    public String getRegisterForm() {
        return ViewConst.REGISTER;
    }


    @GetMapping(URLConst.FORUM_SELECTION)
    public String getAllForums(Model model) {
        List<Forum> forums = forumService.getAllForums();

        model.addAttribute(FORUM, forums);
        return ViewConst.FORUM_SELECTION;
    }

    // not finished
    @GetMapping(URLConst.GET_BOARD_PAGE)
    public String getBoardPage(Model model,
                               @PathVariable String boardName,
                               Integer presentPage, Search search) {
        Long boardPostTotalCount = forumService.getBoardPostTotalCount(boardName, search);

        Paging paging = Utilities.getPaging(boardPostTotalCount, presentPage, postsPerPage);
        List<BoardPost> boardPosts = forumService.getBoardPosts(paging, search, boardName);

        model.addAttribute(BOARD_NAME, boardName);
        model.addAttribute(PAGING, paging);
        model.addAttribute(SEARCH, search);
        model.addAttribute(BOARD_POSTS, boardPosts);

        return ViewConst.BOARD;
    }

//    @GetMapping(URLConst.POST_DETAILS)
//    public String getPostDetails(@PathVariable Long postId) {
//
//        return ViewConst.POST;
//    }

}
