package ohih.town.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.*;
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
    ResourceBundle successMessageSource = SUCCESS_MESSAGES;


    private boolean checkDuplication(String field, String value) {
        Map<String, String> map = new HashMap<>();
        map.put(UtilityConst.FIELD, field);
        map.put(UtilityConst.VALUE, value);

        return userMapper.checkDuplication(map);
    }

    public CheckResult checkValidationAndDuplication(Pattern pattern, String filed, String input,
                                                     String validMessage,
                                                     String invalidMessage, String duplicatedMessage) {
        CheckResult checkResult = new CheckResult();
        boolean isValid = Utilities.checkValidation(pattern, input);
        boolean isDuplicated = checkDuplication(filed, input);

        List<String> messages = new ArrayList<>();

        if (!isValid) {
            checkResult.setValid(false);
            messages.add(invalidMessage);
        }
        if (isDuplicated) {
            checkResult.setDuplicated(false);
            messages.add(duplicatedMessage);
        }

        if (isValid && !isDuplicated) {
            checkResult.setValid(true);
            checkResult.setDuplicated(false);
            messages.add(validMessage);
        }

        checkResult.setMessages(messages);
        return checkResult;
    }

    public CheckResult checkValidation(Pattern pattern, String input,
                                       String validMessage,
                                       String invalidMessage) {
        CheckResult checkResult = new CheckResult();
        checkResult.setValid(Utilities.checkValidation(pattern, input));

        if (checkResult.isValid()) {
            checkResult.setMessages(Collections.singletonList(validMessage));
        } else {
            checkResult.setMessages(Collections.singletonList(invalidMessage));
        }
        return checkResult;
    }

    public CheckResult checkStringEquality(String string1, String string2,
                                           String validMessage,
                                           String invalidMessage) {
        CheckResult checkResult = new CheckResult();

        if (Objects.equals(string1, string2)) {
            checkResult.setValid(true);
            checkResult.setMessages(Collections.singletonList(validMessage));
        } else {
            checkResult.setValid(false);
            checkResult.setMessages(Collections.singletonList(invalidMessage));
        }
        return checkResult;
    }

    private CheckResult checkRegisterRequestNull(RegisterRequest registerRequest) {
        CheckResult checkResult = new CheckResult();
        List<String> messages = new ArrayList<>();

        if (registerRequest == null) {
            messages.add(userErrorMessageSource.getString(REGISTER_REQUEST_NULL));
        } else {
            if (registerRequest.getEmail() == null) {
                messages.add(userErrorMessageSource.getString(USER_EMAIL_NULL));
            }
            if (registerRequest.getUsername() == null) {
                messages.add(userErrorMessageSource.getString(USER_USERNAME_NULL));
            }
            if (registerRequest.getPassword() == null) {
                messages.add(userErrorMessageSource.getString(USER_PASSWORD_NULL));
            }
            if (registerRequest.getConfirmPassword() == null) {
                messages.add(userErrorMessageSource.getString(USER_CONFIRM_PASSWORD_NULL));
            }
        }
        checkResult.setValid(messages.size() == 0);
        checkResult.setMessages(messages);
        return checkResult;
    }

    public CheckResult checkRegisterRequest(String validatedEmail, String authenticatedEmail,
                                            String validatedUsername,
                                            RegisterRequest registerRequest) {
        CheckResult checkResult = new CheckResult();

        // Check for null in register request.
        CheckResult checkRegisterRequestNull = checkRegisterRequestNull(registerRequest);
        if (checkRegisterRequestNull.isValid()) {
            return checkRegisterRequestNull;
        }


        // Check the email and username if they are different from the session values
        List<String> messages = new ArrayList<>();
        if (!Objects.equals(validatedEmail, registerRequest.getEmail())
                || !Objects.equals(authenticatedEmail, registerRequest.getEmail())) {
            messages.add(userErrorMessageSource.getString(USER_EMAIL_EMAIL_MISMATCH));
        }
        if (!Objects.equals(validatedUsername, registerRequest.getUsername())) {
            messages.add(userErrorMessageSource.getString(USER_USERNAME_USERNAME_MISMATCH));
        }


        // Check validation and duplication of fields
        CheckResult checkEmail = checkValidationAndDuplication(ValidationPatterns.EMAIL,
                UserConst.USERNAME, registerRequest.getEmail(),
                USER_EMAIL_VALID, USER_EMAIL_INVALID, USER_EMAIL_DUPLICATED);
        CheckResult checkUsername = checkValidationAndDuplication(ValidationPatterns.USERNAME,
                UserConst.USERNAME, registerRequest.getUsername(),
                USER_USERNAME_VALID, USER_USERNAME_INVALID, USER_USERNAME_DUPLICATED);
        CheckResult checkPassword = checkValidation(ValidationPatterns.PASSWORD,
                registerRequest.getPassword(), USER_PASSWORD_VALID, USER_PASSWORD_INVALID);
        CheckResult checkConfirmPassword = checkStringEquality(registerRequest.getPassword(), registerRequest.getConfirmPassword(),
                USER_CONFIRM_PASSWORD_VALID, USER_CONFIRM_PASSWORD_INVALID);

        if (!checkEmail.isValid() || checkEmail.isDuplicated()) {
            messages.addAll(checkEmail.getMessages());
        }
        if (!checkUsername.isValid() || checkUsername.isDuplicated()) {
            messages.addAll(checkUsername.getMessages());
        }
        if (!checkPassword.isValid() || checkPassword.isDuplicated()) {
            messages.addAll(checkPassword.getMessages());
        }
        if (!checkConfirmPassword.isValid()) {
            messages.addAll(checkConfirmPassword.getMessages());
        }

        if (!registerRequest.isAgreement()) {
            messages.add(userErrorMessageSource.getString(USER_AGREEMENT_MISSING));
        }

        checkResult.setMessages(messages);
        checkResult.setValid(messages.isEmpty());

        return checkEmail;
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
            registerResult.setMessages(Collections.singletonList(successMessageSource.getString(USER_REGISTRATION_SUCCESS)));
            registerResult.setRedirectUrl(URLConst.HOME);
        } catch (SQLException e) {
            registerResult.setMessages(Collections.singletonList(userErrorMessageSource.getString(USER_REGISTER_SQL_EXCEPTION)));
        }
        return registerResult;
    }


    // login
    public LoginResult login(String email, String password) {
        LoginResult loginResult = new LoginResult();
        loginResult.setRedirectUrl(URLConst.LOGIN);

        List<String> messages = new ArrayList<>();

        CheckResult checkEmail = checkValidation(ValidationPatterns.EMAIL, email,
                successMessageSource.getString(USER_EMAIL_VALID), userErrorMessageSource.getString(USER_EMAIL_INVALID));
        CheckResult checkPassword = checkValidation(ValidationPatterns.PASSWORD, password,
                successMessageSource.getString(USER_PASSWORD_VALID), userErrorMessageSource.getString(USER_PASSWORD_INVALID));

        if (!checkEmail.isValid()) {
            messages.addAll(checkEmail.getMessages());
        }
        if (!checkPassword.isValid()) {
            messages.addAll(checkPassword.getMessages());
        }

        if (!messages.isEmpty()) {
            loginResult.setMessage(messages);
            return loginResult;
        }

        UserInfo userInfo = getUserByEmailAndPassword(email, password);
        if (userInfo != null) {
            loginResult.setSuccess(true);
            messages.add(successMessageSource.getString(USER_LOGIN_SUCCESS));
            loginResult.setRedirectUrl(URLConst.HOME);
            loginResult.setUserInfo(userInfo);
        } else {
            messages.add(userErrorMessageSource.getString(USER_LOGIN_FAILURE_INVALID_CREDENTIALS));
        }
        loginResult.setMessage(messages);

        return loginResult;
    }

    private UserInfo getUserByEmailAndPassword(String email, String password) {
        Map<String, String> map = new HashMap<>();
        map.put(UserConst.EMAIL, email);
        map.put(UserConst.PASSWORD, password);

        return userMapper.getUserByEmailAndPassword(map);
    }


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
            profileImageActionResult.setMessage(userErrorMessageSource.getString(UPLOAD_PROFILE_IMAGE_FAILURE));
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
            profileImageActionResult.setMessage(userErrorMessageSource.getString(UPDATE_PROFILE_IMAGE_FAILURE));
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
            profileImageActionResult.setMessage(userErrorMessageSource.getString(DELETE_PROFILE_IMAGE_FAILURE));
        }

        return profileImageActionResult;
    }

    public void updateUsername(UserInfoUpdateResult userInfoUpdateResult, Long userId, String username) {
        Map<String, Object> map = new HashMap<>();
        map.put(UserConst.USER_ID, userId);
        map.put(UserConst.USERNAME, username);

        try {
            if (!userMapper.updateUsername(map)) {
                throw new SQLException();
            }
            userInfoUpdateResult.setSuccess(true);
            userInfoUpdateResult.setMessages(Collections.singletonList(successMessageSource.getString(USERNAME_UPDATE_SUCCESS)));
        } catch (SQLException e) {
            userInfoUpdateResult.setMessages(Collections.singletonList(userErrorMessageSource.getString(UPDATE_USERNAME_FAILURE)));
        }
    }

    public void updatePassword(UserInfoUpdateResult userInfoUpdateResult, Long userId, String password) {
        Map<String, Object> map = new HashMap<>();
        map.put(UserConst.USER_ID, userId);
        map.put(UserConst.PASSWORD, password);

        try {
            if (!userMapper.updatePassword(map)) {
                throw new SQLException();
            }
            userInfoUpdateResult.setSuccess(true);
            userInfoUpdateResult.setMessages(Collections.singletonList(successMessageSource.getString(PASSWORD_UPDATE_SUCCESS)));
        } catch (SQLException e) {
            userInfoUpdateResult.setMessages(Collections.singletonList(userErrorMessageSource.getString(UPDATE_PASSWORD_FAILURE)));
        }
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

    public void updateGuestbookPermission(UserInfoUpdateResult userInfoUpdateResult,
                                          Long userId, GuestbookPermission guestbookPermission) {
        guestbookPermission.setUserId(userId);

        try {
            if (!userMapper.updateGuestbookPermission(guestbookPermission)) {
                throw new SQLException();
            }
            userInfoUpdateResult.setSuccess(true);
            userInfoUpdateResult.setMessages(Collections.singletonList(successMessageSource.getString(GUESTBOOK_PERMISSION_UPDATE_SUCCESS)));
        } catch (SQLException e) {
            userInfoUpdateResult.setMessages(Collections.singletonList(userErrorMessageSource.getString(GUESTBOOK_PERMISSION_UPDATE_FAILURE)));
        }
    }

    public void updateGuestbookActivation(UserInfoUpdateResult userInfoUpdateResult,
                                          Long userId, boolean activation) {
        Map<String, Object> map = new HashMap<>();
        map.put(UserConst.USER_ID, userId);
        map.put(UserConst.ACTIVATION, activation);

        try {
            if (!userMapper.updateGuestbookActivation(map)) {
                throw new SQLException();
            }
            userInfoUpdateResult.setSuccess(true);
            userInfoUpdateResult.setMessages(Collections.singletonList(successMessageSource.getString(GUESTBOOK_ACTIVATION_UPDATE_SUCCESS)));
        } catch (SQLException e) {
            userInfoUpdateResult.setMessages(Collections.singletonList(userErrorMessageSource.getString(GUESTBOOK_ACTIVATION_UPDATE_FAILURE)));
        }
    }
}
