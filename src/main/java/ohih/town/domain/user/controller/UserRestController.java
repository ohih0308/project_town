package ohih.town.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.*;
import ohih.town.domain.SimpleResponse;
import ohih.town.domain.user.dto.*;
import ohih.town.domain.user.service.UserService;
import ohih.town.session.SessionManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import static ohih.town.constants.ErrorsConst.*;
import static ohih.town.constants.SessionConst.VALIDATED_USERNAME;
import static ohih.town.constants.SuccessConst.*;
import static ohih.town.constants.SuccessMessagesResourceBundle.SUCCESS_MESSAGES;
import static ohih.town.constants.URLConst.UPDATE_GUESTBOOK_ACTIVATION;
import static ohih.town.constants.URLConst.UPDATE_GUESTBOOK_PERMISSION;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserRestController {

    private final UserService userService;


    @PostMapping(URLConst.CHECK_USERNAME)
    public CheckResult checkUsername(HttpServletRequest request,
                                     String username) {
        CheckResult checkResult = userService.checkValidationAndDuplication(ValidationPatterns.USERNAME,
                UserConst.USERNAME, username,
                USER_USERNAME_VALID,
                USER_USERNAME_INVALID, USER_USERNAME_DUPLICATED);

        if (checkResult.isValid() && !checkResult.isDuplicated()) {
            SessionManager.setAttributes(request, VALIDATED_USERNAME, username);
        }

        return checkResult;
    }

    @PostMapping(URLConst.CHECK_PASSWORD)
    public CheckResult checkPassword(String password) {
        return userService.checkValidation(ValidationPatterns.PASSWORD,
                UserConst.PASSWORD,
                password,
                USER_PASSWORD_VALID, USER_PASSWORD_INVALID);
    }

    @PostMapping(URLConst.CHECK_CONFIRM_PASSWORD)
    public CheckResult checkConfirmPassword(String password, String confirmPassword) {
        return userService.confirmPassword(password, confirmPassword);
    }

    @PostMapping(URLConst.REGISTER_URL)
    public RegisterResult register(HttpServletRequest request,
                                   RegisterRequest registerRequest) {
        String VALIDATED_EMAIL = (String) SessionManager.getAttributes(request, SessionConst.VALIDATED_EMAIL);
        String VALIDATED_USERNAME = (String) SessionManager.getAttributes(request, SessionConst.VALIDATED_USERNAME);

        RegisterResult requestNullCheckResult = userService.hasNullRegisterRequest(registerRequest);
        if (!requestNullCheckResult.getErrorMessages().isEmpty()) {
            return requestNullCheckResult;
        }
        RegisterResult sessionNullCheckResult = userService.verifySessionValues(VALIDATED_EMAIL, VALIDATED_USERNAME);
        if (!sessionNullCheckResult.getErrorMessages().isEmpty()) {
            return sessionNullCheckResult;
        }
        RegisterResult requestCheckFieldsResult = userService.checkRegisterRequestFields(VALIDATED_EMAIL,
                VALIDATED_USERNAME,
                registerRequest);
        if (!requestCheckFieldsResult.getErrorMessages().isEmpty()) {
            return requestCheckFieldsResult;
        }
        return userService.registerUserExceptionHandler(registerRequest);
    }


    @PostMapping(URLConst.LOGIN)
    public LoginResult login(HttpServletRequest request,
                             String email, String password) {
        LoginResult loginResult = userService.login(email, password);
        SessionManager.setAttributes(request, SessionConst.USER_INFO, loginResult.getUserInfo());

        return loginResult;
    }

    // condition: isLoginInterceptor
    @PostMapping(URLConst.LOGOUT)
    public String logout(HttpServletRequest request) {
        SessionManager.removeAttribute(request, SessionConst.USER_INFO);
        return SUCCESS_MESSAGES.getString(USER_LOGOUT_SUCCESS);
    }


    // condition: isLoginInterceptor
    @PostMapping(URLConst.UPLOAD_PROFILE_IMAGE)
    public ProfileImageActionResult uploadProfileImage(HttpServletRequest request,
                                                       @SessionAttribute(SessionConst.USER_INFO) UserInfo userInfo,
                                                       MultipartFile multipartFile) {
        ProfileImageActionResult profileImageActionResult = userService.uploadProfileImage(multipartFile, userInfo.getUserId());

        userInfo.setDirectory(profileImageActionResult.getProfileImage().getDirectory());
        userInfo.setExtension(profileImageActionResult.getProfileImage().getExtension());

        SessionManager.updateAttribute(request, SessionConst.USER_INFO, userInfo);

        return profileImageActionResult;
    }

    // condition: isLoginInterceptor
    @PostMapping(URLConst.UPDATE_PROFILE_IMAGE)
    public ProfileImageActionResult updateProfileImage(HttpServletRequest request,
                                                       @SessionAttribute(SessionConst.USER_INFO) UserInfo userInfo,
                                                       MultipartFile multipartFile) {
        ProfileImageActionResult profileImageActionResult = userService.updateProfileImage(multipartFile, userInfo.getUserId(), userInfo.getDirectory());

        userInfo.setDirectory(profileImageActionResult.getProfileImage().getDirectory());
        userInfo.setExtension(profileImageActionResult.getProfileImage().getExtension());

        SessionManager.updateAttribute(request, SessionConst.USER_INFO, userInfo);

        return profileImageActionResult;
    }

    // condition: isLoginInterceptor
    @PostMapping(URLConst.DELETE_PROFILE_IMAGE)
    public ProfileImageActionResult deleteProfileImage(HttpServletRequest request,
                                                       @SessionAttribute(SessionConst.USER_INFO) UserInfo userInfo) {
        ProfileImageActionResult profileImageActionResult = userService.deleteProfileImage(userInfo.getUserId(), userInfo.getDirectory());

        userInfo.setDirectory(null);
        userInfo.setExtension(null);

        SessionManager.updateAttribute(request, SessionConst.USER_INFO, userInfo);

        return profileImageActionResult;
    }

    // condition: isLoginInterceptor
    @PostMapping(URLConst.UPDATE_USERNAME)
    public UserInfoUpdateResult updateUsername(@SessionAttribute(SessionConst.USER_INFO) UserInfo userInfo,
                                               String username) {
        UserInfoUpdateResult userInfoUpdateResult = new UserInfoUpdateResult();
        CheckResult checkResult = userService.checkValidationAndDuplication(ValidationPatterns.USERNAME,
                UserConst.USERNAME, username,
                USER_USERNAME_VALID,
                USER_USERNAME_INVALID, USER_USERNAME_DUPLICATED);

        if (checkResult.isValid() && !checkResult.isDuplicated()) {
            userService.updateUsername(userInfoUpdateResult, userInfo.getUserId(), username);
        } else {
            userInfoUpdateResult.setMessages(checkResult.getMessages());
        }
        return userInfoUpdateResult;
    }

    // condition: isLoginInterceptor
    @PostMapping(URLConst.UPDATE_PASSWORD)
    public UserInfoUpdateResult updatePassword(@SessionAttribute(SessionConst.USER_INFO) UserInfo userInfo,
                                               String password) {
        UserInfoUpdateResult userInfoUpdateResult = new UserInfoUpdateResult();
        CheckResult checkResult = userService.checkValidation(ValidationPatterns.PASSWORD,
                UserConst.PASSWORD,
                password,
                USER_PASSWORD_VALID, USER_PASSWORD_INVALID);

        if (checkResult.isValid() && !checkResult.isDuplicated()) {
            userService.updatePassword(userInfoUpdateResult, userInfo.getUserId(), password);
        } else {
            userInfoUpdateResult.setMessages(checkResult.getMessages());
        }
        return userInfoUpdateResult;
    }

    // condition: isLoginInterceptor posts, comments all delete
//    @PostMapping(URLConst.DEACTIVATE)
//    public UserInfoUpdateResult deactivate(@SessionAttribute(SessionConst.USER_INFO) UserInfo userInfo) {
//        UserInfoUpdateResult userInfoUpdateResult = new UserInfoUpdateResult();
//
//        userService.deactivate(userInfoUpdateResult, userInfo.getUserId());
//
//        return userInfoUpdateResult;
//    }


    // condition: isLoginInterceptor
    @PostMapping(UPDATE_GUESTBOOK_PERMISSION)
    public UserInfoUpdateResult updateGuestbookPermissions(@SessionAttribute(SessionConst.USER_INFO) UserInfo userInfo,
                                                           GuestbookPermission guestbookPermission) {
        return userService.updateGuestbookPermission(userInfo.getUserId(), guestbookPermission);
    }

    // condition: isLoginInterceptor
    @PostMapping(UPDATE_GUESTBOOK_ACTIVATION)
    public UserInfoUpdateResult updateGuestbookActivation(@SessionAttribute(SessionConst.USER_INFO) UserInfo userInfo,
                                                          boolean activation) {
        return userService.updateGuestbookActivation(userInfo.getUserId(), activation);
    }
}
