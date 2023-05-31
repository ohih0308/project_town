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

import static ohih.town.constants.URLConst.*;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final BoardServiceImpl boardService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new IsLoginInterceptor())
                .addPathPatterns(URLConst.LOGOUT,
                        UPLOAD_PROFILE_IMAGE,
                        DELETE_PROFILE_IMAGE,
                        UPDATE_USERNAME,
                        UPDATE_PASSWORD,
                        DEACTIVATE_ACCOUNT,
                        UPDATE_GUESTBOOK_PERMISSION,
                        UPDATE_GUESTBOOK_ACTIVATION);

        registry.addInterceptor(new IsAdminInterceptor()).addPathPatterns(
                VERIFY_CATEGORY,
                VERIFY_BOARD,
                CREATE_CATEGORY,
                CREATE_BOARD,
                RENAME_CATEGORY,
                RENAME_BOARD);

        registry.addInterceptor(new IsBoardActivatedInterceptor(boardService)).addPathPatterns(
                UPLOAD_COMMENT,
                ACCESS_PERMISSION_COMMENT,
                DELETE_COMMENT,
                UPLOAD_POST,
                ACCESS_PERMISSION_POST,
                UPDATE_POST,
                DELETE_POST,
                APPRAISE_POST
        );
    }
}
