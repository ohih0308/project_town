package ohih.town.config;

import lombok.RequiredArgsConstructor;
import ohih.town.constants.URLConst;
import ohih.town.interceptor.IsLoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static ohih.town.constants.URLConst.UPDATE_GUESTBOOK_ACTIVATION;
import static ohih.town.constants.URLConst.UPDATE_GUESTBOOK_PERMISSION;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new IsLoginInterceptor())
                .addPathPatterns(URLConst.LOGOUT,
                        URLConst.UPLOAD_PROFILE_IMAGE,
                        URLConst.DELETE_PROFILE_IMAGE,
                        URLConst.UPDATE_USERNAME,
                        URLConst.UPDATE_PASSWORD,
                        URLConst.DEACTIVATE,
                        UPDATE_GUESTBOOK_PERMISSION,
                        UPDATE_GUESTBOOK_ACTIVATION);
    }
}
