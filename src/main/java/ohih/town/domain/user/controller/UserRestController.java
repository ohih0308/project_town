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

import java.io.IOException;
import java.sql.SQLException;

import static ohih.town.constants.ErrorMessagesResourceBundle.*;
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
                USER_ERROR_MESSAGES, SUCCESS_MESSAGES,
                USER_USERNAME_INVALID, USER_USERNAME_DUPLICATED,
                USER_USERNAME_VALID,
                UserConst.USERNAME, username);

        if (checkResult.getIsValid() && !checkResult.getIsDuplicated()) {
            SessionManager.setAttributes(request, VALIDATED_USERNAME, username);

        }

        return checkResult;
    }

    @PostMapping(URLConst.CHECK_PASSWORD)
    public CheckResult checkPassword(String password) {
        return userService.checkValidation(ValidationPatterns.PASSWORD,
                USER_ERROR_MESSAGES, SUCCESS_MESSAGES,
                USER_PASSWORD_INVALID, USER_PASSWORD_VALID,
                password);
    }

    @PostMapping(URLConst.CHECK_CONFIRM_PASSWORD)
    public CheckResult checkConfirmPassword(String password, String confirmPassword) {
        return userService.checkConfirmPassword(password, confirmPassword);
    }

    @PostMapping(URLConst.REGISTER_URL)
    public RegisterResult register(HttpServletRequest request,
                                   RegisterRequest registerRequest) {
        String VALIDATED_EMAIL = (String) SessionManager.getAttributes(request, SessionConst.VALIDATED_EMAIL);
        String AUTHENTICATED_EMAIL = (String) SessionManager.getAttributes(request, SessionConst.AUTHENTICATED_EMAIL);
        String VALIDATED_USERNAME = (String) SessionManager.getAttributes(request, SessionConst.VALIDATED_USERNAME);

        RegisterResult registerResult = userService.validateRegisterRequest(VALIDATED_EMAIL, AUTHENTICATED_EMAIL,
                VALIDATED_USERNAME,
                registerRequest);

        if (registerResult.getErrorFields().isEmpty() && registerResult.getErrorMessages().isEmpty()) {
            userService.registerUser(registerRequest.getEmail(),
                    registerRequest.getUsername(),
                    registerRequest.getPassword());

            registerResult.setSuccess(true);
            registerResult.setRedirectUrl(URLConst.HOME);
            registerResult.setSuccessMessage(SUCCESS_MESSAGES.getString(USER_REGISTRATION_SUCCESS));
        }

        return registerResult;
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
    public SimpleResponse uploadProfileImage(HttpServletRequest request,
                                             @SessionAttribute(SessionConst.USER_INFO) UserInfo userInfo,
                                             MultipartFile multipartFile) {
        SimpleResponse simpleResponse = new SimpleResponse();

        try {
            ProfileImage profileImage;

            if (userService.findProfileImageByUserId(userInfo.getId()) == null) {
                profileImage = userService.uploadProfileImage(multipartFile, userInfo.getId());
            } else {
                profileImage = userService.updateProfileImage(multipartFile, userInfo.getId());
                SessionManager.updateAttribute(request, SessionConst.USER_INFO, profileImage);
            }

            userInfo.setSavedFileName(profileImage.getSavedFileName());
            userInfo.setExtension(profileImage.getExtension());
            userInfo.setDirectory(profileImage.getDirectory());

            SessionManager.updateAttribute(request, SessionConst.USER_INFO, userInfo);
            SessionManager.updateAttribute(request, SessionConst.USER_INFO, profileImage);

            simpleResponse.setSuccess(true);
            simpleResponse.setMessage(SUCCESS_MESSAGES.getString(UPLOAD_PROFILE_IMAGE_SUCCESS));
        } catch (IOException e) {
            simpleResponse.setSuccess(false);
            simpleResponse.setMessage(MAIL_ERROR_MESSAGES.getString(UPLOAD_PROFILE_IMAGE_FAILURE));
        }

        return simpleResponse;
    }

    // condition: isLoginInterceptor
    @PostMapping(URLConst.DELETE_PROFILE_IMAGE)
    public SimpleResponse deleteProfileImage(HttpServletRequest request,
                                             @SessionAttribute(SessionConst.USER_INFO) UserInfo userInfo) {
        SimpleResponse simpleResponse = new SimpleResponse();
        ProfileImage profileImage = userService.findProfileImageByUserId(userInfo.getId());

        if (profileImage == null) {
            simpleResponse.setSuccess(false);
            simpleResponse.setMessage(MAIL_ERROR_MESSAGES.getString(ErrorsConst.DELETE_PROFILE_IMAGE_FAILURE_NOT_UPLOADED));
        } else {
            try {
                userService.deleteProfileImage(profileImage.getDirectory(), userInfo.getId());

                userInfo.setSavedFileName(null);
                userInfo.setExtension(null);
                userInfo.setDirectory(null);

                SessionManager.updateAttribute(request, SessionConst.USER_INFO, userInfo);
            } catch (SQLException e) {
                simpleResponse.setSuccess(false);
                simpleResponse.setMessage(MAIL_ERROR_MESSAGES.getString(ErrorsConst.DELETE_PROFILE_IMAGE_FAILURE));
            }
        }
        return simpleResponse;
    }

    // condition: isLoginInterceptor
    @PostMapping(URLConst.UPDATE_USERNAME)
    public CheckResult updateUsername(@SessionAttribute(SessionConst.USER_INFO) UserInfo userInfo,
                                      String username) {
        CheckResult checkResult = userService.checkValidationAndDuplication(ValidationPatterns.USERNAME,
                USER_ERROR_MESSAGES, SUCCESS_MESSAGES,
                USER_USERNAME_INVALID, USER_USERNAME_DUPLICATED,
                USER_USERNAME_VALID,
                UserConst.USERNAME, username);

        if (checkResult.getIsValid() && checkResult.getIsDuplicated()) {
            try {
                userService.updateUsername(userInfo.getId(), username);
                checkResult.setMessage(SUCCESS_MESSAGES.getString(USERNAME_UPDATE_SUCCESS));
            } catch (SQLException e) {
                checkResult.setMessage(DATABASE_ERROR_MESSAGES.getString(DATABASE_UPDATE_ERROR));
            }
        }
        return checkResult;
    }

    // condition: isLoginInterceptor
    @PostMapping(URLConst.UPDATE_PASSWORD)
    public CheckResult updatePassword(@SessionAttribute(SessionConst.USER_INFO) UserInfo userInfo,
                                      String password) {
        CheckResult checkResult = userService.checkValidation(ValidationPatterns.PASSWORD,
                USER_ERROR_MESSAGES, SUCCESS_MESSAGES,
                USER_PASSWORD_INVALID, USER_PASSWORD_VALID,
                password);

        if (checkResult.getIsValid()) {
            try {
                userService.updatePassword(userInfo.getId(), password);
                checkResult.setMessage(SUCCESS_MESSAGES.getString(PASSWORD_UPDATE_SUCCESS));
            } catch (SQLException e) {
                checkResult.setMessage(DATABASE_ERROR_MESSAGES.getString(DATABASE_UPDATE_ERROR));
            }
        }
        return checkResult;
    }

    // condition: isLoginInterceptor
    @PostMapping(URLConst.DEACTIVATE)
    public SimpleResponse deactivate(@SessionAttribute(SessionConst.USER_INFO) UserInfo userInfo) {
        SimpleResponse simpleResponse = new SimpleResponse();

        try {
            userService.deactivate(userInfo.getId());
            simpleResponse.setMessage(SUCCESS_MESSAGES.getString(DEACTIVATE_SUCCESS));
        } catch (SQLException e) {
            simpleResponse.setSuccess(false);
            simpleResponse.setMessage(DATABASE_ERROR_MESSAGES.getString(DATABASE_DELETE_ERROR));
        }

        return simpleResponse;
    }


    // condition: isLoginInterceptor
    @PostMapping(UPDATE_GUESTBOOK_PERMISSION)
    public SimpleResponse updateGuestbookPermissions(@SessionAttribute(SessionConst.USER_INFO) UserInfo userInfo,
                                                     GuestbookPermission guestbookPermission) {
        SimpleResponse simpleResponse = new SimpleResponse();

        try {
            userService.updateGuestbookPermission(userInfo.getId(), guestbookPermission);
            simpleResponse.setMessage(SUCCESS_MESSAGES.getString(GUESTBOOK_PERMISSION_UPDATE_SUCCESS));
            simpleResponse.setSuccess(true);
        } catch (SQLException e) {
            simpleResponse.setMessage(DATABASE_ERROR_MESSAGES.getString(DATABASE_UPDATE_ERROR));
            simpleResponse.setSuccess(false);
        }

        return simpleResponse;
    }

    // condition: isLoginInterceptor
    @PostMapping(UPDATE_GUESTBOOK_ACTIVATION)
    public SimpleResponse updateGuestbookActivation(@SessionAttribute(SessionConst.USER_INFO) UserInfo userInfo,
                                                    boolean activation) {
        SimpleResponse simpleResponse = new SimpleResponse();

        try {
            userService.updateGuestbookActivation(userInfo.getId(), activation);
            simpleResponse.setSuccess(true);
            simpleResponse.setMessage(SUCCESS_MESSAGES.getString(GUESTBOOK_ACTIVATION_UPDATE_SUCCESS));
        } catch (SQLException e) {
            simpleResponse.setSuccess(false);
            simpleResponse.setMessage(DATABASE_ERROR_MESSAGES.getString(DATABASE_UPDATE_ERROR));
        }

        return simpleResponse;
    }
}