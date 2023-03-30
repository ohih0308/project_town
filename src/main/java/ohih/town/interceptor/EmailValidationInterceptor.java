package ohih.town.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ohih.town.constants.SessionConst;
import ohih.town.constants.UserConst;
import ohih.town.constants.ValidationPatterns;
import ohih.town.domain.user.dto.CheckResult;
import ohih.town.domain.user.service.UserService;
import ohih.town.session.SessionManager;
import org.springframework.web.servlet.HandlerInterceptor;

import static ohih.town.constants.ErrorMessagesResourceBundle.USER_ERROR_MESSAGES;
import static ohih.town.constants.ErrorsConst.USER_EMAIL_DUPLICATED;
import static ohih.town.constants.ErrorsConst.USER_EMAIL_INVALID;
import static ohih.town.constants.SuccessConst.USER_EMAIL_VALID;
import static ohih.town.constants.SuccessMessagesResourceBundle.SUCCESS_MESSAGES;

@RequiredArgsConstructor
public class EmailValidationInterceptor implements HandlerInterceptor {

    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String email = request.getParameter(UserConst.EMAIL);

        CheckResult checkResult = userService.checkValidationAndDuplication(ValidationPatterns.EMAIL,
                USER_ERROR_MESSAGES, SUCCESS_MESSAGES,
                USER_EMAIL_INVALID, USER_EMAIL_DUPLICATED,
                USER_EMAIL_VALID,
                UserConst.EMAIL, email);

        if (checkResult.getIsValid() && !checkResult.getIsDuplicated()) {
            SessionManager.setAttributes(request, SessionConst.VALIDATED_EMAIL, email);
        }

        return true;
    }
}
