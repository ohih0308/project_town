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
import ohih.town.domain.post.service.PostService;
import ohih.town.session.SessionManager;
import ohih.town.utilities.Paging;
import ohih.town.utilities.Search;
import ohih.town.utilities.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

import static ohih.town.constants.ForumConst.*;
import static ohih.town.constants.PagingConst.postsPerPage;
import static ohih.town.constants.PostConst.POST_DETAILS;
import static ohih.town.constants.PostConst.POST_UPDATE_INFO;
import static ohih.town.constants.UtilityConst.PAGING;
import static ohih.town.constants.UtilityConst.SEARCH;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TownController {

    private final ForumService forumService;
    private final PostService postService;

    @Autowired
    ApplicationContext applicationContext;


    @Value("#{verificationMail['mail.from']}")
    private String from;
    @Value("#{verificationMail['mail.verification.subject']}")
    private String subject;
    @Value("#{verificationMail['mail.verification.body']}")
    private String body;

    @GetMapping("/beans")
    public String beans() {
        String[] beans = applicationContext.getBeanDefinitionNames();

        for (String bean : beans) {
            log.info("bean name = {}", bean);
        }

        System.out.println("from = " + from);
        System.out.println("subject = " + subject);
        System.out.println("body = " + body.replace("${verification-code}", "hello"));
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


    @GetMapping(URLConst.LOGIN)
    public String login() {
        return ViewConst.LOGIN;
    }


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


    @GetMapping(URLConst.POST_DETAILS)
    public String getPostDetails(Model model,
                                 @PathVariable Long postId) {
        model.addAttribute(POST_DETAILS, postService.getPostDetailsByPostId(postId));
        return ViewConst.POST_DETAILS;
    }

    @GetMapping(URLConst.UPLOAD_POST)
    public String getUploadPostForm() {
        return ViewConst.UPLOAD_POST_FORM;
    }

    @PostMapping(URLConst.UPDATE_POST_FORM)
    public String getUpdatePostForm(HttpServletRequest request, Model model,
                                    Long postId) {
        Long permittedPostId = (Long) SessionManager.getAttributes(request, SessionConst.ACCESS_PERMITTED_POST_ID);

        if (permittedPostId == null || permittedPostId != postId) {
            return "redirect:/post/" + postId;
        } else {
            model.addAttribute(POST_UPDATE_INFO, postService.getPostUpdateInfoByPostId(postId));
            return ViewConst.UPDATE_POST_FORM;
        }
    }

    @PostMapping(URLConst.DELETE_POST_FORM)
    public String deletePost(HttpServletRequest request,
                             @PathVariable Long postId) {
        String boardName = forumService.getBoardNameByPostId(postId);
        Long permittedPostId = (Long) SessionManager.getAttributes(request, SessionConst.ACCESS_PERMITTED_POST_ID);

        if (permittedPostId == null || permittedPostId != postId) {
            return "redirect:/post/" + postId;
        } else {
// comment firsts
            return "redirect:/board/" + boardName;
        }
    }
}
