package ohih.town;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.DomainConst;
import ohih.town.constants.URLConst;
import ohih.town.constants.UtilityConst;
import ohih.town.constants.ViewConst;
import ohih.town.domain.board.service.BoardServiceImpl;
import ohih.town.domain.comment.dto.Comment;
import ohih.town.domain.comment.service.CommentServiceImpl;
import ohih.town.domain.guestbook.dto.Guestbook;
import ohih.town.domain.guestbook.service.GuestbookServiceImpl;
import ohih.town.domain.post.dto.SimplePost;
import ohih.town.domain.post.service.PostServiceImpl;
import ohih.town.domain.user.dto.UserInfo;
import ohih.town.utilities.Paging;
import ohih.town.utilities.Search;
import ohih.town.utilities.Utilities;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

import static ohih.town.constants.DomainConst.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TownController {

    private final BoardServiceImpl boardService;
    private final PostServiceImpl postService;
    private final CommentServiceImpl commentService;
    private final GuestbookServiceImpl guestbookService;


    @GetMapping(URLConst.HOME)
    public String home() {
        return ViewConst.HOME;
    }

    @GetMapping(URLConst.REGISTER)
    public String getRegisterForm() {
        return ViewConst.REGISTER;
    }

    @GetMapping(URLConst.LOGIN)
    public String login() {
        return ViewConst.LOGIN;
    }

    @GetMapping(URLConst.BOARD_SELECTION)
    public String selectBoard(Model model) {
        model.addAttribute(BOARD_LIST, boardService.getBoards());
        return ViewConst.BOARD_SELECTION;
    }

    @GetMapping(URLConst.BOARD)
    public String getBoard(Model model,
                           Long boardId, Integer presentPage, Search search) {
        Long totalCount = postService.countPosts(boardId, search);

        Paging paging = Utilities.getPaging(totalCount, presentPage, UtilityConst.POSTS_PER_PAGE);
        List<SimplePost> simplePosts = postService.getPosts(boardId, paging, search);

        model.addAttribute(BOARD_NAME, boardService.getBoardName(boardId));
        model.addAttribute(UtilityConst.PAGING, paging);
        model.addAttribute(UtilityConst.SEARCH, search);
        model.addAttribute(DomainConst.SIMPLE_POSTS, simplePosts);

        return ViewConst.BOARD;
    }


    @GetMapping(URLConst.POST_DETAILS)
    public String getPostDetails(Model model, @PathVariable Long postId) {
        model.addAttribute(POST_DETAILS, postService.getPostDetails(postId));
        return ViewConst.POST_DETAILS;
    }

    @GetMapping(URLConst.UPLOAD_POST_FORM)
    public String uploadPost(Model model,
                             Long boardId) {
        model.addAttribute(BOARD_ID, boardId);
        model.addAttribute(BOARD_NAME, boardService.getBoardName(boardId));

        return ViewConst.UPLOAD_POST_FORM;
    }

    @GetMapping(URLConst.UPDATE_POST_FORM)
    public String updatePost(Model model,
                             Long postId) {
        model.addAttribute(POST_CONTENT, postService.getPostContent(postId));

        return ViewConst.UPDATE_POST_FORM;
    }


    @GetMapping(URLConst.MY_PAGE)
    public String getMyPage() {
        return ViewConst.MY_PAGE;
    }

    @GetMapping(URLConst.MY_POSTS)
    public String getMyPosts(Model model,
                             @SessionAttribute UserInfo userInfo,
                             Integer presentPage, Search search) {
        Long totalCount = postService.countMyPosts(userInfo.getUserId(), search);
        Paging paging = Utilities.getPaging(totalCount, presentPage, UtilityConst.POSTS_PER_PAGE);
        List<SimplePost> simplePosts = postService.getMyPosts(userInfo.getUserId(), paging, search);

        model.addAttribute(UtilityConst.PAGING, paging);
        model.addAttribute(UtilityConst.SEARCH, search);
        model.addAttribute(DomainConst.SIMPLE_POSTS, simplePosts);

        return ViewConst.MY_POSTS;
    }

    @GetMapping(URLConst.MY_COMMENTS)
    public String getMyComments(Model model,
                                @SessionAttribute UserInfo userInfo,
                                Integer presentPage, Search search) {
        Long totalCount = commentService.countMyComments(userInfo.getUserId(), search);
        Paging paging = Utilities.getPaging(totalCount, presentPage, UtilityConst.POSTS_PER_PAGE);
        List<Comment> comments = commentService.getMyComments(userInfo.getUserId(), paging, search);

        model.addAttribute(UtilityConst.PAGING, paging);
        model.addAttribute(UtilityConst.SEARCH, search);
        model.addAttribute(COMMENTS, comments);

        return ViewConst.MY_COMMENTS;
    }

    @GetMapping(URLConst.GUESTBOOK_POSTS)
    public String getGuestbookPosts(Long ownerId, Model model,
                                    @SessionAttribute UserInfo userInfo,
                                    Integer presentPage, Search search) {
        Long totalCount = guestbookService.countPosts(ownerId, search);
        Paging paging = Utilities.getPaging(totalCount, presentPage, UtilityConst.POSTS_PER_PAGE);
        List<Guestbook> posts = guestbookService.getPosts(ownerId,
                userInfo != null ? userInfo.getUserId() : null,
                paging);

        model.addAttribute(UtilityConst.PAGING, paging);
        model.addAttribute(UtilityConst.SEARCH, search);
        model.addAttribute(GUESTBOOK_POSTS, posts);

        return ViewConst.GUESTBOOK;
    }
}
