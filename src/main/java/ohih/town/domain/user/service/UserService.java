package ohih.town.domain.user.service;


import ohih.town.domain.user.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ResourceBundle;

import static ohih.town.constants.ErrorMessageResourceBundle.COMMON_ERROR_MESSAGES;
import static ohih.town.constants.ErrorMessageResourceBundle.USER_ERROR_MESSAGES;
import static ohih.town.constants.SuccessMessagesResourceBundle.SUCCESS_MESSAGES;

public interface UserService {

    ResourceBundle userErrorMessageSource = USER_ERROR_MESSAGES;
    ResourceBundle commonErrorMessageSource = COMMON_ERROR_MESSAGES;
    ResourceBundle successMessageSource = SUCCESS_MESSAGES;


    /*
     * class list:
     *   RegisterRequest, RegisterResult,
     *   LoginResult, UserInfo,
     *   ProfileImage, ProfileImageActionResult,
     *   UserInfoUpdateResult,
     *   GuestbookPerMission
     * */


    boolean isDuplicated(String field, String value);

    boolean confirmPassword(String password, String confirmPassword);

    boolean hasNull(RegisterRequest registerRequest);

    // null, duplication, validation, confirm password check
    RegisterResult checkRegisterRequest();

    // register and initialize guestbook configs
    RegisterResult registerUser(RegisterRequest registerRequest);


    LoginResult login(String email, String password);


    ProfileImage extractProfileImageFromRequest(MultipartFile multipartFile, Long userId);

    ProfileImageActionResult uploadProfileImage(MultipartFile multipartFile, Long userId);

    ProfileImageActionResult updateProfileImage(MultipartFile multipartFile, Long userId);

    ProfileImageActionResult deleteProfileImage(String directory);

    UserInfoUpdateResult updateUsername(Long userId, String username);

    UserInfoUpdateResult updatePassword(Long userId, String password);

    UserInfoUpdateResult updateGuestbookPermission(Long userId, GuestbookPermission guestbookPermission);

    UserInfoUpdateResult updateGuestbookActivation(Long userId, boolean activation);

}
