package ohih.town.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.*;
import ohih.town.domain.SimpleResponse;
import ohih.town.domain.user.dto.*;
import ohih.town.domain.user.mapper.UserMapper;
import ohih.town.utilities.Utilities;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

import static ohih.town.constants.ErrorMessageResourceBundle.COMMON_ERROR_MESSAGES;
import static ohih.town.constants.ErrorMessageResourceBundle.USER_ERROR_MESSAGES;
import static ohih.town.constants.ErrorsConst.*;
import static ohih.town.constants.SuccessConst.*;
import static ohih.town.constants.SuccessMessagesResourceBundle.SUCCESS_MESSAGES;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserMapper userMapper;

    ResourceBundle userErrorMessageSource = USER_ERROR_MESSAGES;
    ResourceBundle commonErrorMessageSource = COMMON_ERROR_MESSAGES;
    ResourceBundle successMessageSource = SUCCESS_MESSAGES;


    private boolean checkDuplication(String field, String value) {
        Map<String, String> map = new HashMap<>();
        map.put(UtilityConst.FIELD, field);
        map.put(UtilityConst.VALUE, value);

        return userMapper.checkDuplication(map);
    }

    public CheckResult checkValidationAndDuplication(Pattern pattern, String field, String input,
                                                     String validMessage,
                                                     String invalidMessage, String duplicatedMessage) {
        CheckResult checkResult = new CheckResult();
        boolean isValid = Utilities.checkValidation(pattern, input);
        boolean isDuplicated = checkDuplication(field, input);

        Map<String, String> messages = new HashMap<>();

        if (!isValid) {
            messages.put(field, invalidMessage);
        }
        if (isDuplicated) {
            messages.put(field, duplicatedMessage);
        }

        if (isValid && !isDuplicated) {
            messages.put(field, validMessage);
        }

        checkResult.setMessages(messages);

        return checkResult;
    }

    public CheckResult checkValidation(Pattern pattern, String field, String input,
                                       String validMessage,
                                       String invalidMessage) {
        CheckResult checkResult = new CheckResult();
        Map<String, String> messages = new HashMap<>();

        checkResult.setValid(Utilities.checkValidation(pattern, input));

        if (checkResult.isValid()) {
            messages.put(field, validMessage);
        } else {
            messages.put(field, invalidMessage);
        }

        checkResult.setMessages(messages);
        return checkResult;
    }

    public CheckResult confirmPassword(String password, String confirmPassword) {
        CheckResult checkResult = new CheckResult();
        Map<String, String> messages = new HashMap<>();

        if (Objects.equals(password, confirmPassword)) {
            checkResult.setValid(true);
            messages.put(UserConst.CONFIRM_PASSWORD, successMessageSource.getString(USER_CONFIRM_PASSWORD_VALID));
        } else {
            messages.put(UserConst.CONFIRM_PASSWORD, userErrorMessageSource.getString(USER_CONFIRM_PASSWORD_INVALID));
        }
        checkResult.setMessages(messages);

        return checkResult;
    }

    public RegisterResult hasNullRegisterRequest(RegisterRequest registerRequest) {
        RegisterResult registerResult = new RegisterResult();
        Map<String, String> errorMessages = new HashMap<>();

        if (registerRequest == null) {
            errorMessages.put(UserConst.REGISTER_REQUEST, userErrorMessageSource.getString(USER_REGISTER_REQUEST_NULL));
        } else {
            if (registerRequest.getEmail() == null) {
                errorMessages.put(UserConst.EMAIL, userErrorMessageSource.getString(USER_EMAIL_NULL));
            }
            if (registerRequest.getUsername() == null) {
                errorMessages.put(UserConst.USERNAME, userErrorMessageSource.getString(USER_USERNAME_NULL));
            }
            if (registerRequest.getPassword() == null) {
                errorMessages.put(UserConst.PASSWORD, userErrorMessageSource.getString(USER_PASSWORD_NULL));
            }
            if (registerRequest.getConfirmPassword() == null) {
                errorMessages.put(UserConst.CONFIRM_PASSWORD, userErrorMessageSource.getString(USER_CONFIRM_PASSWORD_NULL));
            }
        }

        if (!errorMessages.isEmpty()) {
            registerResult.setErrorMessages(errorMessages);
            registerResult.setResultMessage(userErrorMessageSource.getString(USER_REGISTER_REQUEST_NULL));
        }

        return registerResult;
    }

    public RegisterResult verifySessionValues(String validatedEmail,
                                              String authenticatedEmail,
                                              String validatedUsername) {
        RegisterResult registerResult = new RegisterResult();
        Map<String, String> errorMessages = new HashMap<>();

        if (validatedEmail == null) {
            errorMessages.put(UserConst.VALIDATED_EMAIL, userErrorMessageSource.getString(USER_EMAIL_VALIDATED_NULL));
        }
        if (authenticatedEmail == null) {
            errorMessages.put(UserConst.AUTHENTICATED_EMAIL, userErrorMessageSource.getString(USER_EMAIL_AUTHENTICATED_NULL));
        }
        if (validatedUsername == null) {
            errorMessages.put(UserConst.VALIDATED_USERNAME, userErrorMessageSource.getString(USER_USERNAME_VALIDATED_NULL));
        }

        if (!errorMessages.isEmpty()) {
            registerResult.setErrorMessages(errorMessages);
            registerResult.setResultMessage(commonErrorMessageSource.getString(USER_REGISTER_FAILED));
        }

        return registerResult;
    }

    public RegisterResult checkRegisterRequestFields(String validatedEmail,
                                                     String authenticatedEmail,
                                                     String validatedUsername,
                                                     RegisterRequest registerRequest) {
        RegisterResult registerResult = new RegisterResult();
        Map<String, String> errorMessages = new HashMap<>();

        if (!validatedEmail.equals(registerRequest.getEmail())
                || !authenticatedEmail.equals(registerRequest.getEmail())) {
            errorMessages.put(UserConst.EMAIL, userErrorMessageSource.getString(USER_EMAIL_EMAIL_MISMATCH));
        }
        if (!validatedUsername.equals(registerRequest.getUsername())) {
            errorMessages.put(UserConst.USERNAME, userErrorMessageSource.getString(USER_USERNAME_USERNAME_MISMATCH));
        }

        CheckResult checkEmail = checkValidationAndDuplication(ValidationPatterns.EMAIL,
                UserConst.EMAIL, registerRequest.getEmail(),
                successMessageSource.getString(USER_EMAIL_VALID),
                userErrorMessageSource.getString(USER_EMAIL_INVALID),
                userErrorMessageSource.getString(USER_EMAIL_DUPLICATED));
        CheckResult checkUsername = checkValidationAndDuplication(ValidationPatterns.USERNAME,
                UserConst.USERNAME, registerRequest.getUsername(),
                successMessageSource.getString(USER_USERNAME_VALID),
                userErrorMessageSource.getString(USER_USERNAME_INVALID),
                userErrorMessageSource.getString(USER_USERNAME_DUPLICATED));
        CheckResult checkPassword = checkValidation(ValidationPatterns.PASSWORD,
                UserConst.PASSWORD, registerRequest.getPassword(),
                successMessageSource.getString(USER_PASSWORD_VALID),
                userErrorMessageSource.getString(USER_PASSWORD_INVALID));
        CheckResult checkConfirmPassword = confirmPassword(registerRequest.getPassword(),
                registerRequest.getConfirmPassword());

        if (!checkEmail.isValid()) {
            errorMessages.put(UserConst.EMAIL, userErrorMessageSource.getString(USER_EMAIL_INVALID));
        }
        if (checkEmail.isDuplicated()) {
            errorMessages.put(UserConst.EMAIL, userErrorMessageSource.getString(USER_EMAIL_DUPLICATED));
        }
        if (!checkUsername.isValid()) {
            errorMessages.put(UserConst.USERNAME, userErrorMessageSource.getString(USER_USERNAME_INVALID));
        }
        if (checkUsername.isDuplicated()) {
            errorMessages.put(UserConst.USERNAME, userErrorMessageSource.getString(USER_USERNAME_DUPLICATED));
        }
        if (!checkPassword.isValid()) {
            errorMessages.put(UserConst.PASSWORD, userErrorMessageSource.getString(USER_PASSWORD_INVALID));
        }
        if (!checkConfirmPassword.isValid()) {
            errorMessages.put(UserConst.CONFIRM_PASSWORD, userErrorMessageSource.getString(USER_CONFIRM_PASSWORD_INVALID));
        }
        if (!registerRequest.isAgreement()) {
            errorMessages.put(UserConst.AGREEMENT, userErrorMessageSource.getString(USER_AGREEMENT_MISSING));
        }

        if (!errorMessages.isEmpty()) {
            registerResult.setErrorMessages(errorMessages);
            registerResult.setResultMessage(commonErrorMessageSource.getString(INVALID_ACCESS_ERROR));
        }

        return registerResult;
    }


    @Transactional
    public void registerUser(RegisterRequest registerRequest) throws SQLException {
        if (!userMapper.registerUser(registerRequest) ||
                !userMapper.initializeGuestBookConfig(registerRequest.getUserId())) {
            throw new SQLException();
        }
    }

    public RegisterResult registerUserExceptionHandler(RegisterRequest registerRequest) {
        RegisterResult registerResult = new RegisterResult();
        try {
            registerUser(registerRequest);
            registerResult.setSuccess(true);
            registerResult.setResultMessage(successMessageSource.getString(USER_REGISTRATION_SUCCESS));
            registerResult.setRedirectUrl(URLConst.HOME);
        } catch (SQLException e) {
            registerResult.setResultMessage(userErrorMessageSource.getString(USER_REGISTER_SQL_EXCEPTION));
        }
        return registerResult;
    }


    // login
    public LoginResult login(String email, String password) {
        LoginResult loginResult = new LoginResult();
        Map<String, String> errorMessages = new HashMap<>();

        CheckResult checkEmail = checkValidation(ValidationPatterns.EMAIL, UserConst.EMAIL, email,
                successMessageSource.getString(USER_EMAIL_VALID),
                userErrorMessageSource.getString(USER_EMAIL_INVALID));
        CheckResult checkPassword = checkValidation(ValidationPatterns.PASSWORD, UserConst.PASSWORD, password,
                successMessageSource.getString(USER_PASSWORD_VALID),
                userErrorMessageSource.getString(USER_PASSWORD_INVALID));

        if (!checkEmail.isValid()) {
            errorMessages.putAll(checkEmail.getMessages());
        }
        if (!checkPassword.isValid()) {
            errorMessages.putAll(checkPassword.getMessages());
        }

        if (!checkEmail.isValid() || !checkPassword.isValid()) {
            loginResult.setErrorMessages(errorMessages);
            loginResult.setResultMessage(userErrorMessageSource.getString(USER_LOGIN_FAILURE_INVALID_CREDENTIALS));
            return loginResult;
        }

        UserInfo userInfo = getUserByEmailAndPassword(email, password);

        if (userInfo != null) {
            loginResult.setSuccess(true);
            loginResult.setResultMessage(successMessageSource.getString(USER_LOGIN_SUCCESS));
            loginResult.setRedirectUrl(URLConst.HOME);
            loginResult.setUserInfo(userInfo);
        } else {
            loginResult.setResultMessage(userErrorMessageSource.getString(USER_LOGIN_FAILURE_INVALID_CREDENTIALS));
        }

        return loginResult;
    }

    private UserInfo getUserByEmailAndPassword(String email, String password) {
        Map<String, String> map = new HashMap<>();
        map.put(UserConst.EMAIL, email);
        map.put(UserConst.PASSWORD, password);

        return userMapper.getUserByEmailAndPassword(map);
    }


    // profile image
    private ProfileImage extractProfileImageFromRequest(MultipartFile multipartFile, Long userId)
            throws NullPointerException {
        String filePath = ConfigurationResourceBundle.FILE_PATHS.getString(ConfigurationConst.PROFILE_IMAGE_DIRECTORY);

        String savedFileName = Utilities.createCode(UtilityConst.UUID_FULL_INDEX);
        String originalFilename = multipartFile.getOriginalFilename();
        assert originalFilename != null;
        String extension = filePath.substring(originalFilename.lastIndexOf(".") + 1);
        String directory = filePath + "/" + savedFileName + "." + extension;

        ProfileImage profileImage = new ProfileImage();
        profileImage.setSavedFileName(savedFileName);
        profileImage.setUserId(userId);
        profileImage.setOriginalFileName(originalFilename);
        profileImage.setExtension(extension);
        profileImage.setDirectory(directory);

        return profileImage;
    }

    @Transactional(rollbackFor = {SQLException.class, IOException.class})
    public ProfileImageActionResult uploadProfileImage(MultipartFile multipartFile, Long userId) {
        ProfileImageActionResult profileImageActionResult = new ProfileImageActionResult();

        try {
            ProfileImage profileImage = extractProfileImageFromRequest(multipartFile, userId);
            if (!userMapper.uploadProfileImage(profileImage)) {
                throw new SQLException();
            }
            multipartFile.transferTo(new File(profileImage.getDirectory()));
            profileImageActionResult.setSuccess(true);
            profileImageActionResult.setProfileImage(profileImage);
        } catch (NullPointerException e) {
            log.info("UserService.uploadProfileImage occurs NullPointerException. userId = {}", userId);
        } catch (SQLException e) {
            log.info("UserService.uploadProfileImage occurs SQLException. userId = {}", userId);
        } catch (IOException e) {
            log.info("UserService.uploadProfileImage occurs IOException. userId = {}", userId);
        }

        if (profileImageActionResult.isSuccess()) {
            profileImageActionResult.setMessage(successMessageSource.getString(UPLOAD_PROFILE_IMAGE_SUCCESS));
        } else {
            profileImageActionResult.setMessage(userErrorMessageSource.getString(USER_PROFILE_IMAGE_UPLOAD_FAILURE));
        }

        return profileImageActionResult;
    }

    @Transactional(rollbackFor = {SQLException.class, IOException.class})
    public ProfileImageActionResult updateProfileImage(MultipartFile multipartFile,
                                                       Long userId, String oldFileDirectory) {
        ProfileImageActionResult profileImageActionResult = new ProfileImageActionResult();

        try {
            ProfileImage profileImage = extractProfileImageFromRequest(multipartFile, userId);
            if (!userMapper.updateProfileImage(profileImage)) {
                throw new SQLException();
            }

            if (!new File(oldFileDirectory).delete()) {
                log.info("old profile image delete failure. directory = {}", oldFileDirectory);
            }
            multipartFile.transferTo(new File(profileImage.getDirectory()));

            profileImageActionResult.setProfileImage(profileImage);
        } catch (NullPointerException e) {
            log.info("UserService.updateProfileImage occurs NullPointerException. userId = {}", userId);
        } catch (SQLException e) {
            log.info("UserService.updateProfileImage occurs SQLException. userId = {}", userId);
        } catch (IOException e) {
            log.info("UserService.updateProfileImage occurs IOException. userId = {}", userId);
        }

        if (profileImageActionResult.isSuccess()) {
            profileImageActionResult.setMessage(successMessageSource.getString(UPDATE_PROFILE_IMAGE_SUCCESS));
        } else {
            profileImageActionResult.setMessage(userErrorMessageSource.getString(USER_PROFILE_IMAGE_UPDATE_FAILURE));
        }

        return profileImageActionResult;
    }

    @Transactional(rollbackFor = {SQLException.class, IOException.class})
    public ProfileImageActionResult deleteProfileImage(Long userId, String oldFileDirectory) {
        ProfileImageActionResult profileImageActionResult = new ProfileImageActionResult();
        try {
            if (!userMapper.deleteProfileImage(userId)) {
                throw new SQLException();
            }
            if (!new File(oldFileDirectory).delete()) {
                throw new IOException();
            }
            profileImageActionResult.setSuccess(true);
        } catch (NullPointerException e) {
            log.info("UserService.deleteProfileImage occurs NullPointerException. userId = {}", userId);
        } catch (SQLException e) {
            log.info("UserService.deleteProfileImage occurs SQLException. userId = {}", userId);
        } catch (IOException e) {
            log.info("UserService.deleteProfileImage occurs IOException. userId = {}", userId);
        }

        if (profileImageActionResult.isSuccess()) {
            profileImageActionResult.setMessage(successMessageSource.getString(DELETE_PROFILE_IMAGE_SUCCESS));
        } else {
            profileImageActionResult.setMessage(userErrorMessageSource.getString(USER_PROFILE_IMAGE_DELETE_FAILURE));
        }

        return profileImageActionResult;
    }

    public void updateUsername(UserInfoUpdateResult userInfoUpdateResult, Long userId, String username) {
        Map<String, Object> map = new HashMap<>();
        map.put(UserConst.USER_ID, userId);
        map.put(UserConst.USERNAME, username);

        Map<String, String> messages = new HashMap<>();
        try {
            if (!userMapper.updateUsername(map)) {
                throw new SQLException();
            }
            userInfoUpdateResult.setSuccess(true);
            messages.put(UserConst.USERNAME, successMessageSource.getString(USERNAME_UPDATE_SUCCESS));
        } catch (SQLException e) {
            messages.put(UserConst.USERNAME, userErrorMessageSource.getString(USER_UPDATE_USERNAME_FAILURE));
        }

        userInfoUpdateResult.setMessages(messages);
    }

    public void updatePassword(UserInfoUpdateResult userInfoUpdateResult, Long userId, String password) {
        Map<String, Object> map = new HashMap<>();
        map.put(UserConst.USER_ID, userId);
        map.put(UserConst.PASSWORD, password);

        Map<String, String> messages = new HashMap<>();
        try {
            if (!userMapper.updatePassword(map)) {
                throw new SQLException();
            }
            userInfoUpdateResult.setSuccess(true);
            messages.put(UserConst.PASSWORD, successMessageSource.getString(PASSWORD_UPDATE_SUCCESS));
        } catch (SQLException e) {
            messages.put(UserConst.PASSWORD, userErrorMessageSource.getString(USER_UPDATE_PASSWORD_FAILURE));
        }

        userInfoUpdateResult.setMessages(messages);
    }

//    public void deactivate(UserInfoUpdateResult userInfoUpdateResult, Long userId) {
//        try {
//            if (!userMapper.deactivate(userId)) {
//                throw new SQLException();
//            }
//            userInfoUpdateResult.setSuccess(true);
//            userInfoUpdateResult.setMessages(Collections.singletonList(successMessageSource.getString(DEACTIVATE_SUCCESS)));
//        } catch (SQLException e) {
//            userInfoUpdateResult.setMessages(Collections.singletonList(userErrorMessageSource.getString(DEACTIVATE_ACCOUNT_FAILURE)));
//        }
//
//    }

    public UserInfoUpdateResult updateGuestbookPermission(Long userId, GuestbookPermission guestbookPermission) {
        UserInfoUpdateResult userInfoUpdateResult = new UserInfoUpdateResult();
        Map<String, String> messages = new HashMap<>();
        guestbookPermission.setUserId(userId);

        try {
            if (!userMapper.updateGuestbookPermission(guestbookPermission)) {
                throw new SQLException();
            }
            userInfoUpdateResult.setSuccess(true);
            messages.put(UserConst.GUESTBOOK_PERMISSION, successMessageSource.getString(GUESTBOOK_PERMISSION_UPDATE_SUCCESS));
        } catch (SQLException e) {
            messages.put(UserConst.GUESTBOOK_PERMISSION, userErrorMessageSource.getString(USER_GUESTBOOK_PERMISSION_UPDATE_FAILURE));
        }

        return userInfoUpdateResult;
    }

    public UserInfoUpdateResult updateGuestbookActivation(Long userId, boolean activation) {
        UserInfoUpdateResult userInfoUpdateResult = new UserInfoUpdateResult();
        Map<String, String> messages = new HashMap<>();

        Map<String, Object> map = new HashMap<>();
        map.put(UserConst.USER_ID, userId);
        map.put(UserConst.GUESTBOOK_ACTIVATION, activation);

        try {
            if (!userMapper.updateGuestbookActivation(map)) {
                throw new SQLException();
            }
            userInfoUpdateResult.setSuccess(true);
            messages.put(UserConst.GUESTBOOK_ACTIVATION, successMessageSource.getString(GUESTBOOK_ACTIVATION_UPDATE_SUCCESS));
        } catch (SQLException e) {
            messages.put(UserConst.GUESTBOOK_ACTIVATION, userErrorMessageSource.getString(USER_GUESTBOOK_ACTIVATION_UPDATE_FAILURE));
        }

        return userInfoUpdateResult;
    }
}
