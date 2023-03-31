package ohih.town.config;

import lombok.RequiredArgsConstructor;
import ohih.town.constants.URLConst;
import ohih.town.domain.user.service.UserService;
import ohih.town.interceptor.IsLoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final UserService userService;
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new IsLoginInterceptor())
//                .addPathPatterns(URLConst.SEND_VERIFICATION_CODE);
//
//    }
}
