package ohih.town;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.DomainConst;
import ohih.town.constants.URLConst;
import ohih.town.constants.UtilityConst;
import ohih.town.constants.ViewConst;
import ohih.town.domain.board.dto.BoardPost;
import ohih.town.domain.board.service.BoardServiceImpl;
import ohih.town.domain.post.service.PostServiceImpl;
import ohih.town.utilities.Paging;
import ohih.town.utilities.Search;
import ohih.town.utilities.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import static ohih.town.constants.DomainConst.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TownController {

    private final BoardServiceImpl boardService;
    private final PostServiceImpl postService;


    @GetMapping(URLConst.HOME)
    public String home() {
        return ViewConst.HOME;
    }

    @GetMapping(URLConst.REGISTER)
    public String getRegisterForm() {
        return ViewConst.REGISTER;
    }

    @GetMapping(URLConst.BOARD_SELECTION)
    public String selectBoard(Model model) {
        model.addAttribute(BOARD_LIST, boardService.getBoards());
        return ViewConst.BOARD_SELECTION;
    }

    @GetMapping(URLConst.BOARD)
    public String getBoard(Model model,
                           Long boardId, Integer presentPage, Search search) {
        Long totalCount = boardService.countPosts(boardId, search);

        Paging paging = Utilities.getPaging(totalCount, presentPage, UtilityConst.POSTS_PER_PAGE);
        List<BoardPost> boardPosts = boardService.getPosts(boardId, paging, search);

        model.addAttribute(BOARD_NAME, boardService.getBoardName(boardId));
        model.addAttribute(UtilityConst.PAGING, paging);
        model.addAttribute(UtilityConst.SEARCH, search);
        model.addAttribute(DomainConst.BOARD_POSTS, boardPosts);

        return ViewConst.BOARD;
    }

    @GetMapping(URLConst.UPLOAD_POST)
    public String uploadPost(Model model,
                             Long boardId) {
        model.addAttribute(BOARD_ID, boardId);
        model.addAttribute(BOARD_NAME, boardService.getBoardName(boardId));

        return ViewConst.UPLOAD_POST_FORM;
    }

    @GetMapping(URLConst.UPDATE_POST)
    public String updatePost(Model model,
                             Long postId) {
        model.addAttribute(POST_CONTENT, postService.getPostContent(postId));

        return ViewConst.UPDATE_POST_FORM;
    }


}
