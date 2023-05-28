package ohih.town.domain.user.service;


import ohih.town.domain.VerificationResult;
import ohih.town.domain.user.dto.*;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    /*
     * class list:
     *   RegisterRequest, RegisterResult,
     *   LoginResult, UserInfo,
     *   ProfileImage, ProfileImageActionResult,
     *   UserInfoUpdateResult,
     *   GuestbookPerMission
     * */


    MailSendResult sendVerificationCode(String email, String verificationCode);

    VerificationResult checkEmailVerificationCode(String EMAIL_VERIFICATION_REQUEST,
                                                  String EMAIL_VERIFICATION_CODE,
                                                  String email,
                                                  String verificationCode);

    boolean isDuplicated(String field, String value);

    VerificationResult verifyEmail(String email);

    VerificationResult verifyUsername(String username);

    VerificationResult verifyPassword(String password);

    VerificationResult verifyPasswordConfirmation(String password, String confirmPassword);

    VerificationResult verifyRegisterRequest(RegisterRequest registerRequest, String verifiedEmail);

    boolean hasNull(RegisterRequest registerRequest);


    // register and initialize guestbook configs
    RegisterResult registerUser(RegisterRequest registerRequest, String verifiedEmail);

    LoginResult login(String email, String password);


    ProfileImage extractProfileImageFromRequest(MultipartFile multipartFile, Long userId);

    ProfileImageResult uploadProfileImage(MultipartFile multipartFile, Long userId);

    ProfileImageResult updateProfileImage(MultipartFile multipartFile, Long userId, String directory);

    ProfileImageResult deleteProfileImage(String directory);

    UserInfoUpdateResult updateUsername(Long userId, String username);

    UserInfoUpdateResult updatePassword(Long userId, String password);

    UserInfoUpdateResult updateGuestbookPermission(GuestbookPermission guestbookPermission);

    UserInfoUpdateResult updateGuestbookActivation(Long userId, boolean activation);

    UserInfoUpdateResult deactivateAccount(Long userId, String directory);


    boolean deactivatePosts(Long userId);

    boolean deactivateComments(Long userId);

    boolean deactivateGuestbookPosts(Long userId);

    boolean deactivateGuestbookComments(Long userId);

}
