package ohih.town.config;

import lombok.RequiredArgsConstructor;
import ohih.town.constants.URLConst;
import ohih.town.domain.board.service.BoardServiceImpl;
import ohih.town.interceptors.IsAdminInterceptor;
import ohih.town.interceptors.IsBoardActivatedInterceptor;
import ohih.town.interceptors.IsLoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static ohih.town.constants.URLConst.UPDATE_GUESTBOOK_ACTIVATION;
import static ohih.town.constants.URLConst.UPDATE_GUESTBOOK_PERMISSION;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final BoardServiceImpl boardService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new IsLoginInterceptor())
                .addPathPatterns(URLConst.LOGOUT,
                        URLConst.UPLOAD_PROFILE_IMAGE,
                        URLConst.DELETE_PROFILE_IMAGE,
                        URLConst.UPDATE_USERNAME,
                        URLConst.UPDATE_PASSWORD,
                        URLConst.DEACTIVATE_ACCOUNT,
                        UPDATE_GUESTBOOK_PERMISSION,
                        UPDATE_GUESTBOOK_ACTIVATION);

        registry.addInterceptor(new IsAdminInterceptor()).addPathPatterns(
                URLConst.VERIFY_CATEGORY,
                URLConst.VERIFY_BOARD,
                URLConst.CREATE_CATEGORY,
                URLConst.CREATE_BOARD,
                URLConst.RENAME_CATEGORY,
                URLConst.RENAME_BOARD);

        registry.addInterceptor(new IsBoardActivatedInterceptor(boardService));
    }
}
