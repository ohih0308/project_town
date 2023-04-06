package ohih.town.domain.user.service;

import lombok.RequiredArgsConstructor;
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

import static ohih.town.constants.ErrorMessageResourceBundle.COMMON_ERROR_MESSAGES;
import static ohih.town.constants.ErrorMessageResourceBundle.USER_ERROR_MESSAGES;
import static ohih.town.constants.ErrorsConst.*;
import static ohih.town.constants.SuccessConst.*;
import static ohih.town.constants.SuccessMessagesResourceBundle.SUCCESS_MESSAGES;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;


    private Boolean isFieldDuplicated(String field, String value) {
        Map map = new HashMap();
        map.put(UtilityConst.FIELD, field);
        map.put(UtilityConst.VALUE, value);

        return userMapper.isFiledDuplicated(map);
    }

    private CheckResult checkField(Boolean isValid, Boolean isDuplicated,
                                   ResourceBundle errorMessageSource, ResourceBundle successMessageSource,
                                   String invalidMessage, String duplicatedMessage,
                                   String validMessage) {
        CheckResult checkResult = new CheckResult();

        if (!isValid) {
            checkResult.setIsValid(false);
            if (isDuplicated) {
                checkResult.setIsDuplicated(true);
                checkResult.setMessage(errorMessageSource.getString(invalidMessage) +
                        "\n" +
                        errorMessageSource.getString(duplicatedMessage));
            } else {
                checkResult.setIsDuplicated(false);
                checkResult.setMessage(errorMessageSource.getString(invalidMessage));
            }
        } else {
            checkResult.setIsValid(true);
            if (!isDuplicated) {
                // valid && not duplicated - success
                checkResult.setIsDuplicated(false);
                checkResult.setMessage(successMessageSource.getString(validMessage));
            } else {
                // valid && duplicated - failure
                checkResult.setIsDuplicated(true);
                checkResult.setMessage(errorMessageSource.getString(duplicatedMessage));
            }
        }

        return checkResult;
    }

    // If fields have null value
    private RegisterRequestResult checkRegisterRequestNull(String VALIDATED_EMAIL, String AUTHENTICATED_EMAIL,
                                                           String VALIDATED_USERNAME,
                                                           RegisterRequest registerRequest) {
        RegisterRequestResult registerRequestResult = new RegisterRequestResult();
        registerRequestResult.setSuccess(false);

        List<Map<String, String>> errorFields = new ArrayList<>();
        List<Map<String, String>> errorMessages = new ArrayList<>();


        if (VALIDATED_EMAIL == null || AUTHENTICATED_EMAIL == null) {
            Map<String, String> message = new HashMap<>();
            message.put(UserConst.EMAIL, COMMON_ERROR_MESSAGES.getString(INVALID_ACCESS_ERROR));

            errorMessages.add(message);
        }

        if (VALIDATED_USERNAME == null) {
            Map<String, String> message = new HashMap<>();
            message.put(UserConst.USERNAME, COMMON_ERROR_MESSAGES.getString(INVALID_ACCESS_ERROR));

            errorMessages.add(message);
        }

        if (registerRequest.getEmail() == null) {
            Map<String, String> field = new HashMap<>();
            field.put(UserConst.EMAIL, null);
            Map<String, String> message = new HashMap<>();
            message.put(UserConst.EMAIL, USER_ERROR_MESSAGES.getString(USER_EMAIL_NULL));

            errorFields.add(field);
            errorMessages.add(message);
        }

        if (registerRequest.getUsername() == null) {
            Map<String, String> field = new HashMap<>();
            field.put(UserConst.USERNAME, null);
            Map<String, String> message = new HashMap<>();
            message.put(UserConst.USERNAME, USER_ERROR_MESSAGES.getString(USER_USERNAME_NULL));

            errorFields.add(field);
            errorMessages.add(message);
        }

        if (registerRequest.getPassword() == null) {
            Map<String, String> field = new HashMap<>();
            field.put(UserConst.PASSWORD, null);
            Map<String, String> message = new HashMap<>();
            message.put(UserConst.PASSWORD, USER_ERROR_MESSAGES.getString(USER_PASSWORD_NULL));

            errorFields.add(field);
            errorMessages.add(message);
        }

        if (registerRequest.getConfirmPassword() == null) {
            Map<String, String> field = new HashMap<>();
            field.put(UserConst.CONFIRM_PASSWORD, null);
            Map<String, String> message = new HashMap<>();
            message.put(UserConst.CONFIRM_PASSWORD, USER_ERROR_MESSAGES.getString(USER_CONFIRM_PASSWORD_NULL));

            errorFields.add(field);
            errorMessages.add(message);
        }

        if (!registerRequest.getAgreement()) {
            Map<String, String> field = new HashMap<>();
            field.put(UserConst.AGREEMENT, null);
            Map<String, String> message = new HashMap<>();
            message.put(UserConst.AGREEMENT, USER_ERROR_MESSAGES.getString(USER_AGREEMENT_MISSING));

            errorFields.add(field);
            errorMessages.add(message);
        }


        registerRequestResult.setErrorFields(errorFields);
        registerRequestResult.setErrorMessages(errorMessages);
        return registerRequestResult;
    }

    // If fields not valid
    private RegisterRequestResult checkRegisterRequestValid(RegisterRequest registerRequest) {
        RegisterRequestResult registerRequestResult = new RegisterRequestResult();

        List<Map<String, String>> errorFields = new ArrayList<>();
        List<Map<String, String>> errorMessages = new ArrayList<>();

        CheckResult checkEmail = checkValidationAndDuplication(ValidationPatterns.EMAIL,
                USER_ERROR_MESSAGES, SUCCESS_MESSAGES,
                USER_EMAIL_INVALID, USER_EMAIL_DUPLICATED,
                USER_EMAIL_VALID,
                UserConst.EMAIL, registerRequest.getEmail()
        );

        CheckResult checkUsername = checkValidationAndDuplication(ValidationPatterns.USERNAME,
                USER_ERROR_MESSAGES, SUCCESS_MESSAGES,
                USER_USERNAME_INVALID, USER_USERNAME_DUPLICATED,
                USER_USERNAME_VALID,
                UserConst.USERNAME, registerRequest.getUsername());

        CheckResult checkPassword = checkValidation(ValidationPatterns.PASSWORD,
                USER_ERROR_MESSAGES, SUCCESS_MESSAGES,
                USER_PASSWORD_INVALID, USER_PASSWORD_VALID,
                registerRequest.getPassword());

        CheckResult checkConfirmPassword = checkConfirmPassword(registerRequest.getPassword(), registerRequest.getConfirmPassword());


        if (!checkEmail.getIsValid() || checkEmail.getIsDuplicated()) {
            Map<String, String> field = new HashMap<>();
            field.put(UserConst.EMAIL, registerRequest.getEmail());
            Map<String, String> message = new HashMap<>();
            message.put(UserConst.EMAIL, checkEmail.getMessage());

            errorFields.add(field);
            errorMessages.add(message);
        }

        if (!checkUsername.getIsValid() || checkUsername.getIsDuplicated()) {
            Map<String, String> field = new HashMap<>();
            field.put(UserConst.USERNAME, registerRequest.getUsername());
            Map<String, String> message = new HashMap<>();
            message.put(UserConst.USERNAME, checkUsername.getMessage());

            errorFields.add(field);
            errorMessages.add(message);
        }

        if (!checkPassword.getIsValid()) {
            Map<String, String> field = new HashMap<>();
            field.put(UserConst.PASSWORD, registerRequest.getPassword());
            Map<String, String> message = new HashMap<>();
            message.put(UserConst.PASSWORD, checkPassword.getMessage());

            errorFields.add(field);
            errorMessages.add(message);
        }

        if (!checkConfirmPassword.getIsValid()) {
            Map<String, String> field = new HashMap<>();
            field.put(UserConst.CONFIRM_PASSWORD, registerRequest.getConfirmPassword());
            Map<String, String> message = new HashMap<>();
            message.put(UserConst.CONFIRM_PASSWORD, checkConfirmPassword.getMessage());

            errorFields.add(field);
            errorMessages.add(message);
        }

        registerRequestResult.setErrorFields(errorFields);
        registerRequestResult.setErrorMessages(errorMessages);

        return registerRequestResult;
    }


    public CheckResult checkValidation(Pattern pattern,
                                       ResourceBundle errorMessageSource, ResourceBundle successMessageSource,
                                       String invalidMessage,
                                       String validMessage,
                                       String input) {
        CheckResult checkResult = new CheckResult();

        boolean isValid = Utilities.isValidPattern(pattern, input);

        if (isValid) {
            checkResult.setIsValid(true);
            checkResult.setMessage(successMessageSource.getString(validMessage));
        } else {
            checkResult.setIsValid(false);
            checkResult.setMessage(errorMessageSource.getString(invalidMessage));
        }

        return checkResult;
    }

    public CheckResult checkValidationAndDuplication(Pattern pattern,
                                                     ResourceBundle errorMessageSource, ResourceBundle successMessageSource,
                                                     String invalidMessage, String duplicatedMessage,
                                                     String validMessage,
                                                     String field, String input) {
        Boolean isValid = Utilities.isValidPattern(pattern, input);
        Boolean isDuplicated = isFieldDuplicated(field, input);


        return checkField(isValid, isDuplicated,
                errorMessageSource, successMessageSource,
                invalidMessage, duplicatedMessage
                , validMessage);
    }

    public CheckResult checkConfirmPassword(String password, String confirmPassword) {
        CheckResult checkResult = new CheckResult();

        if (password == null || confirmPassword == null) {
            checkResult.setIsValid(false);
            checkResult.setMessage(USER_ERROR_MESSAGES.getString(USER_CONFIRM_PASSWORD_NULL));

            return checkResult;
        }

        if (password.equals(confirmPassword)) {
            checkResult.setIsValid(true);
            checkResult.setMessage(SUCCESS_MESSAGES.getString(USER_CONFIRM_PASSWORD_VALID));
        } else {
            checkResult.setIsValid(false);
            checkResult.setMessage(USER_ERROR_MESSAGES.getString(USER_CONFIRM_PASSWORD_INVALID));
        }

        return checkResult;
    }


    public RegisterRequestResult validateRegisterRequest(String VALIDATED_EMAIL, String AUTHENTICATED_EMAIL,
                                                         String VALIDATED_USERNAME,
                                                         RegisterRequest registerRequest) {
        RegisterRequestResult registerRequestResult = new RegisterRequestResult();

        RegisterRequestResult registerRequestResultNullCheck = checkRegisterRequestNull(
                VALIDATED_EMAIL, AUTHENTICATED_EMAIL,
                VALIDATED_USERNAME,
                registerRequest);

        if (!registerRequestResultNullCheck.getErrorFields().isEmpty()
                || !registerRequestResultNullCheck.getErrorMessages().isEmpty()) {
            return registerRequestResultNullCheck;
        }


        List<Map<String, String>> errorFields = new ArrayList<>();
        List<Map<String, String>> errorMessages = new ArrayList<>();


        // If the register request form has different values than the session uploaded values
        if (!Objects.equals(VALIDATED_EMAIL, registerRequest.getEmail())
                || !Objects.equals(AUTHENTICATED_EMAIL, registerRequest.getEmail())) {
            Map<String, String> field = new HashMap<>();
            field.put(UserConst.EMAIL, registerRequest.getEmail());

            Map<String, String> message = new HashMap<>();
            message.put(UserConst.EMAIL, USER_ERROR_MESSAGES.getString(USER_EMAIL_EMAIL_MISMATCH));

            errorFields.add(field);
            errorMessages.add(message);
        }

        if (!Objects.equals(VALIDATED_USERNAME, registerRequest.getUsername())) {
            Map<String, String> field = new HashMap<>();
            field.put(UserConst.USERNAME, registerRequest.getUsername());

            Map<String, String> message = new HashMap<>();
            message.put(UserConst.USERNAME, USER_ERROR_MESSAGES.getString(USER_USERNAME_USERNAME_MISMATCH));

            errorFields.add(field);
            errorMessages.add(message);
        }


        // validation and duplication check
        RegisterRequestResult registerRequestResultValidCheck = checkRegisterRequestValid(registerRequest);

        if (!registerRequestResultValidCheck.getErrorFields().isEmpty()) {
            errorFields.addAll(registerRequestResultValidCheck.getErrorFields());
        }

        if (!registerRequestResultValidCheck.getErrorMessages().isEmpty()) {
            errorMessages.addAll(registerRequestResultValidCheck.getErrorMessages());
        }

        registerRequestResult.setErrorFields(errorFields);
        registerRequestResult.setErrorMessages(errorMessages);

        return registerRequestResult;
    }

    @Transactional
    public void registerUser(String email, String username, String password) throws SQLException {
        RegisterUser registerUser = new RegisterUser(email, username, password);

        if (!userMapper.registerUser(registerUser) || !userMapper.initGuestbookConfig(registerUser.getUserId())) {
            throw new SQLException();
        }
    }

    public void registerUserExceptionHandler(RegisterRequestResult registerRequestResult,
                                                              RegisterRequest registerRequest) {
        try {
            registerUser(registerRequest.getEmail(), registerRequest.getUsername(), registerRequest.getPassword());

            registerRequestResult.setSuccess(true);
            registerRequestResult.setRedirectUrl(URLConst.HOME);
            registerRequestResult.setSuccessMessage(SUCCESS_MESSAGES.getString(USER_REGISTRATION_SUCCESS));
        } catch (SQLException e) {
            registerRequestResult.setSuccess(false);

            registerRequestResult.setErrorMessages(
                    Collections.singletonList(
                            Collections.singletonMap(null, USER_ERROR_MESSAGES.getString(USER_REGISTER_SQL_EXCEPTION))));
        }
    }

    public LoginResult login(String email, String password) {
        LoginResult loginResult = new LoginResult();
        CheckResult checkEmail = checkValidation(ValidationPatterns.EMAIL,
                USER_ERROR_MESSAGES, SUCCESS_MESSAGES,
                USER_EMAIL_INVALID, USER_EMAIL_VALID,
                email);

        CheckResult checkPassword = checkValidation(ValidationPatterns.PASSWORD,
                USER_ERROR_MESSAGES, SUCCESS_MESSAGES,
                USER_PASSWORD_INVALID, USER_PASSWORD_VALID,
                password);

        if (!checkEmail.getIsValid() || !checkPassword.getIsValid()) {
            loginResult.setMessage(USER_ERROR_MESSAGES.getString(USER_LOGIN_FAILURE_INVALID_CREDENTIALS));
            loginResult.setRedirectUrl(URLConst.LOGIN);

            return loginResult;
        }

        UserInfo userInfo = getUserByEmailAndPassword(email, password);
        if (userInfo != null) {
            loginResult.setSuccess(true);
            loginResult.setMessage(SUCCESS_MESSAGES.getString(USER_LOGIN_SUCCESS));
            loginResult.setRedirectUrl(URLConst.HOME);
            loginResult.setUserInfo(userInfo);
        }

        return loginResult;
    }


    private UserInfo getUserByEmailAndPassword(String email, String password) {
        Map<String, String> map = new HashMap<>();
        map.put(UserConst.EMAIL, email);
        map.put(UserConst.PASSWORD, password);

        return userMapper.getUserByEmailAndPassword(map);
    }

    public ProfileImage findProfileImageByUserId(Long userId) {
        return userMapper.findProfileImageByUserId(userId);
    }


    private ProfileImage setProfileImage(MultipartFile multipartFile, Long userId) {
        String filePath = ConfigurationResourceBundle.FILE_PATHS.getString(ConfigurationConst.FILE_PATHS);

        String savedFileName = Utilities.createCode(36);
        String originalFilename = multipartFile.getOriginalFilename();
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

    @Transactional
    public ProfileImage uploadProfileImage(MultipartFile multipartFile, Long userId) throws IOException {
        ProfileImage profileImage = setProfileImage(multipartFile, userId);

        File file = new File(profileImage.getDirectory());
        multipartFile.transferTo(file);

        userMapper.uploadProfileImage(profileImage);

        return profileImage;
    }

    private String findProfileImageDirectoryByUserId(Long userId) {
        return userMapper.findProfileImageDirectoryByUserId(userId);
    }

    @Transactional
    public ProfileImage updateProfileImage(MultipartFile multipartFile, Long userId) throws IOException {
        ProfileImage newProfileImage = setProfileImage(multipartFile, userId);

        File oldFile = new File(findProfileImageDirectoryByUserId(userId));
        oldFile.delete();

        File newFile = new File(newProfileImage.getDirectory());
        multipartFile.transferTo(newFile);

        userMapper.updateProfileImage(newProfileImage);

        return newProfileImage;
    }


    @Transactional
    public void deleteProfileImage(String directory, Long userId) throws SQLException {
        File file = new File(directory);

        file.delete();

        if (userMapper.deleteProfileImage(userId) != 1) {
            throw new SQLException();
        }
    }


    public void updateUsername(Long userId, String username) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put(UserConst.USER_ID, userId);
        map.put(UserConst.USERNAME, username);

        if (!userMapper.updateUsername(map)) {
            throw new SQLException();
        }
    }

    public void updatePassword(Long userId, String password) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put(UserConst.USER_ID, userId);
        map.put(UserConst.PASSWORD, password);

        if (!userMapper.updatePassword(map)) {
            throw new SQLException();
        }
    }

    public void deactivate(Long userId) throws SQLException {
        if (!userMapper.deactivate(userId)) {
            throw new SQLException();
        }
    }


    public void updateGuestbookPermission(Long userId, GuestbookPermission guestbookPermission) throws SQLException {
        guestbookPermission.setUserId(userId);

        if (!userMapper.updateGuestbookPermission(guestbookPermission)) {
            throw new SQLException();
        }
    }

    public void updateGuestbookActivation(Long userId, boolean activation) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put(UserConst.USER_ID, userId);
        map.put(UserConst.ACTIVATION, activation);

        if (!userMapper.updateGuestbookActivation(map)) {
            throw new SQLException();
        }
    }
}
