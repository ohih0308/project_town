package ohih.town.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ohih.town.constants.SessionConst;
import ohih.town.domain.user.dto.UserInfo;
import ohih.town.session.SessionManager;
import org.springframework.web.servlet.HandlerInterceptor;

public class IsLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean isLogin = false;

        UserInfo userInfo = (UserInfo) SessionManager.getAttributes(request, SessionConst.USER_INFO);

        if (userInfo != null) {
            isLogin = true;
        }

        return isLogin;
    }
}
