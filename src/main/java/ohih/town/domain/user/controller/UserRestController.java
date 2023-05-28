package ohih.town.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.SessionConst;
import ohih.town.constants.URLConst;
import ohih.town.constants.UtilityConst;
import ohih.town.domain.VerificationResult;
import ohih.town.domain.user.dto.*;
import ohih.town.domain.user.service.UserService;
import ohih.town.domain.user.service.UserServiceImpl;
import ohih.town.session.SessionManager;
import ohih.town.utilities.Utilities;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserRestController {

    private final UserServiceImpl userService;


    @PostMapping(URLConst.SEND_VERIFICATION_CODE)
    public MailSendResult sendVerificationCode(HttpServletRequest request, String email) {
        String verificationCode = Utilities.createCode(UtilityConst.UUID_FULL_INDEX);
        MailSendResult mailSendResult = userService.sendVerificationCode(email, verificationCode);

        if (mailSendResult.isSent()) {
            SessionManager.setAttributes(request, SessionConst.EMAIL_VERIFICATION_REQUEST, email);
            SessionManager.setAttributes(request, SessionConst.EMAIL_VERIFICATION_CODE, verificationCode);
        }

        return mailSendResult;
    }

    @PostMapping(URLConst.VERIFY_EMAIL)
    public VerificationResult verifyEmail(HttpServletRequest request,
                                          String email, String verificationCode) {
        String EMAIL_VERIFICATION_REQUEST = (String) SessionManager.getAttributes(request, SessionConst.EMAIL_VERIFICATION_REQUEST);
        String EMAIL_VERIFICATION_CODE = (String) SessionManager.getAttributes(request, SessionConst.EMAIL_VERIFICATION_CODE);

        VerificationResult verificationResult = userService.checkEmailVerificationCode(EMAIL_VERIFICATION_REQUEST, EMAIL_VERIFICATION_CODE, email, verificationCode);

        if (verificationResult.isVerified()) {
            SessionManager.removeAttribute(request, SessionConst.EMAIL_VERIFICATION_REQUEST);
            SessionManager.removeAttribute(request, SessionConst.EMAIL_VERIFICATION_CODE);

            SessionManager.setAttributes(request, SessionConst.VERIFIED_EMAIL, verificationResult.getVerifiedValue());
        }

        return verificationResult;
    }

    @PostMapping(URLConst.VERIFY_USERNAME)
    public VerificationResult verifyUsername(String username) {
        return userService.verifyUsername(username);
    }

    @PostMapping(URLConst.VERIFY_PASSWORD)
    public VerificationResult verifyPassword(String password) {
        return userService.verifyPassword(password);
    }

    @PostMapping(URLConst.VERIFY_PASSWORD_CONFIRMATION)
    public VerificationResult verifyPasswordConfirmation(String password, String passwordConfirmation) {
        return userService.verifyPasswordConfirmation(password, passwordConfirmation);
    }

    @PostMapping(URLConst.REGISTER)
    public RegisterResult register(HttpServletRequest request, RegisterRequest registerRequest) {
        String verifiedEmail = (String) SessionManager.getAttributes(request, SessionConst.EMAIL_VERIFICATION_REQUEST);

        RegisterResult registerResult = userService.registerUser(registerRequest, verifiedEmail);

        if (registerResult.isRegistered()) {
            SessionManager.removeAttribute(request, SessionConst.EMAIL_VERIFICATION_REQUEST);
        }
        return registerResult;
    }

    @PostMapping(URLConst.LOGIN)
    public LoginResult login(HttpServletRequest request,
                             String email, String password) {
        LoginResult loginResult = userService.login(email, password);

        if (loginResult.isLoggedIn()) {
            SessionManager.setAttributes(request, SessionConst.USER_INFO, loginResult.getUserInfo());
        }
        return loginResult;
    }

    @PostMapping(URLConst.LOGOUT)
    public void logout(HttpServletRequest request) {
        SessionManager.removeAttribute(request, SessionConst.USER_INFO);
    }

    @PostMapping(URLConst.UPLOAD_PROFILE_IMAGE)
    public ProfileImageResult uploadProfileImage(HttpServletRequest request,
                                                 @NotNull @SessionAttribute UserInfo userInfo,
                                                 MultipartFile multipartFile) {
        ProfileImageResult profileImageResult =
                userService.uploadProfileImage(multipartFile, userInfo.getUserId());

        if (profileImageResult.isSuccess()) {
            userInfo.setDirectory(profileImageResult.getProfileImageDirectory());
            SessionManager.updateAttribute(request, SessionConst.USER_INFO, userInfo);
        }

        return profileImageResult;
    }

    @PostMapping(URLConst.UPDATE_PROFILE_IMAGE)
    public ProfileImageResult updateProfileImage(HttpServletRequest request,
                                                 @NotNull @SessionAttribute UserInfo userInfo,
                                                 MultipartFile multipartFile) {
        ProfileImageResult profileImageResult =
                userService.updateProfileImage(multipartFile, userInfo.getUserId(), userInfo.getDirectory());

        if (profileImageResult.isSuccess()) {
            userInfo.setDirectory(profileImageResult.getProfileImageDirectory());
            SessionManager.updateAttribute(request, SessionConst.USER_INFO, userInfo);
        }

        return profileImageResult;
    }

    @PostMapping(URLConst.DELETE_PROFILE_IMAGE)
    public ProfileImageResult deleteProfileImage(HttpServletRequest request,
                                                 @NotNull @SessionAttribute UserInfo userInfo) {
        ProfileImageResult profileImageResult = userService.deleteProfileImage(userInfo.getDirectory());

        if (profileImageResult.isSuccess()) {
            userInfo.setDirectory(profileImageResult.getProfileImageDirectory());
            SessionManager.updateAttribute(request, SessionConst.USER_INFO, userInfo);
        }

        return profileImageResult;
    }

    @PostMapping(URLConst.UPDATE_USERNAME)
    public UserInfoUpdateResult updateUsername(HttpServletRequest request,
                                               @NotNull @SessionAttribute UserInfo userInfo,
                                               String username) {
        UserInfoUpdateResult userInfoUpdateResult = userService.updateUsername(userInfo.getUserId(), username);

        if (userInfoUpdateResult.isSuccess()) {
            userInfo.setUsername(username);
            SessionManager.updateAttribute(request, SessionConst.USER_INFO, userInfo);
        }

        return userInfoUpdateResult;
    }

    @PostMapping(URLConst.UPDATE_PASSWORD)
    public UserInfoUpdateResult updatePassword(@NotNull @SessionAttribute UserInfo userInfo,
                                               String password) {
        return userService.updatePassword(userInfo.getUserId(), password);
    }

    @PostMapping(URLConst.UPDATE_GUESTBOOK_ACTIVATION)
    public UserInfoUpdateResult deactivateGuestbook(@NotNull @SessionAttribute UserInfo userInfo,
                                                    boolean isActivated) {
        return userService.updateGuestbookActivation(userInfo.getUserId(), isActivated);
    }


    @PostMapping(URLConst.DEACTIVATE_ACCOUNT)
    public UserInfoUpdateResult deactivateAccount(@NotNull @SessionAttribute UserInfo userInfo) {
        return userService.deactivateAccount(userInfo.getUserId(), userInfo.getDirectory());
    }
}
