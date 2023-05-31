package ohih.town.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ohih.town.constants.DomainConst;
import ohih.town.constants.SessionConst;
import ohih.town.domain.user.dto.UserInfo;
import ohih.town.session.SessionManager;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

public class IsAdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfo userInfo = (UserInfo) SessionManager.getAttributes(request, SessionConst.USER_INFO);

        if (userInfo == null) {
            return false;
        }

        return Objects.equals(userInfo.getUserType(), DomainConst.USER_TYPE_ADMIN);
    }
}
