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

import static ohih.town.constants.ErrorMessagesResourceBundle.USER_ERROR_MESSAGES;
import static ohih.town.constants.ErrorsConst.*;
import static ohih.town.constants.SuccessConst.*;
import static ohih.town.constants.SuccessMessagesResourceBundle.SUCCESS_MESSAGES;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;


    public Boolean isFieldDuplicated(String field, String value) {
        Map map = new HashMap();
        map.put(UtilityConst.FILED, field);
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
            checkResult.setIsDuplicated(true);

            if (isDuplicated) {
                checkResult.setMessage(errorMessageSource.getString(invalidMessage) +
                        errorMessageSource.getString(duplicatedMessage));
            } else {
                checkResult.setMessage(errorMessageSource.getString(invalidMessage));
            }
        } else {
            checkResult.setIsValid(true);
            checkResult.setIsDuplicated(true);

            if (!isDuplicated) {
                // valid && not duplicated - success
                checkResult.setIsDuplicated(false);
                checkResult.setMessage(successMessageSource.getString(validMessage));
            } else {
                // valid && duplicated - failure
                checkResult.setMessage(errorMessageSource.getString(duplicatedMessage));
            }
        }

        return checkResult;
    }

    // If fields have null value
    private RegisterResult checkRegisterResultNull(String VALIDATED_EMAIL, String AUTHENTICATED_EMAIL,
                                                   String VALIDATED_USERNAME,
                                                   RegisterRequest registerRequest) {
        RegisterResult registerResult = new RegisterResult();
        registerResult.setSuccess(false);

        List<Map<String, String>> errorFields = new ArrayList<>();
        List<Map<String, String>> errorMessages = new ArrayList<>();


        if (VALIDATED_EMAIL == null || AUTHENTICATED_EMAIL == null || registerRequest.getEmail() == null) {
            Map<String, String> field = new HashMap<>();
            field.put(UserConst.EMAIL, null);
            Map<String, String> message = new HashMap<>();
            message.put(UserConst.EMAIL, USER_ERROR_MESSAGES.getString(USER_EMAIL_NULL));

            errorFields.add(field);
            errorMessages.add(message);
        }

        if (VALIDATED_USERNAME == null || registerRequest.getUsername() == null) {
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

        if (registerRequest.getConfirmPassword() == null || !registerRequest.getAgreement()) {
            Map<String, String> field = new HashMap<>();
            field.put(UserConst.AGREEMENT, null);
            Map<String, String> message = new HashMap<>();
            message.put(UserConst.AGREEMENT, USER_ERROR_MESSAGES.getString(USER_AGREEMENT_MISSING));

            errorFields.add(field);
            errorMessages.add(message);
        }


        return registerResult;
    }

    // If fields not valid
    private RegisterResult checkRegisterResultValid(RegisterRequest registerRequest) {
        RegisterResult registerResult = new RegisterResult();

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

        registerResult.setErrorFields(errorFields);
        registerResult.setErrorMessages(errorMessages);

        return registerResult;
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

        CheckResult checkResult = checkField(isValid, isDuplicated,
                errorMessageSource, successMessageSource,
                invalidMessage, duplicatedMessage
                , validMessage);

        return checkResult;
    }

    public CheckResult checkConfirmPassword(String password, String confirmPassword) {
        CheckResult checkResult = new CheckResult();

        if (password == null || confirmPassword == null) {
            checkResult.setIsValid(false);
            checkResult.setMessage(USER_ERROR_MESSAGES.getString(USER_CONFIRM_PASSWORD_NULL));
        }

        if (password.equals(confirmPassword)) {
            checkResult.setIsValid(true);
            checkResult.setMessage(USER_CONFIRM_PASSWORD_VALID);
        } else {
            checkResult.setIsValid(false);
            checkResult.setMessage(USER_ERROR_MESSAGES.getString(USER_CONFIRM_PASSWORD_INVALID));
        }

        return checkResult;
    }


    public RegisterResult validateRegisterRequest(String VALIDATED_EMAIL, String AUTHENTICATED_EMAIL,
                                                  String VALIDATED_USERNAME,
                                                  RegisterRequest registerRequest) {
        RegisterResult registerResult = new RegisterResult();

        RegisterResult registerResultNullCheck = checkRegisterResultNull(
                VALIDATED_EMAIL, AUTHENTICATED_EMAIL,
                VALIDATED_USERNAME,
                registerRequest);

        List<Map<String, String>> errorFields = new ArrayList<>(registerResultNullCheck.getErrorFields());
        List<Map<String, String>> errorMessages = new ArrayList<>(registerResultNullCheck.getErrorMessages());


        // If the register request form has different values than the session uploaded values
        if (!VALIDATED_EMAIL.equals(registerRequest.getEmail())
                || !AUTHENTICATED_EMAIL.equals(registerRequest.getEmail())) {
            Map<String, String> field = new HashMap<>();
            field.put(UserConst.EMAIL, registerRequest.getEmail());

            Map<String, String> message = new HashMap<>();
            message.put(UserConst.EMAIL, USER_ERROR_MESSAGES.getString(USER_EMAIL_EMAIL_MISMATCH));

            errorFields.add(field);
            errorMessages.add(message);
        }

        if (!VALIDATED_USERNAME.equals(registerRequest.getUsername())) {
            Map<String, String> field = new HashMap<>();
            field.put(UserConst.USERNAME, registerRequest.getUsername());

            Map<String, String> message = new HashMap<>();
            message.put(UserConst.USERNAME, USER_ERROR_MESSAGES.getString(USER_USERNAME_USERNAME_MISMATCH));

            errorFields.add(field);
            errorMessages.add(message);
        }


        RegisterResult registerResultValidCheck = checkRegisterResultValid(registerRequest);

        if (!registerResultValidCheck.getErrorFields().isEmpty()) {
            errorFields.addAll(registerResultValidCheck.getErrorFields());
        }

        if (!registerResultValidCheck.getErrorMessages().isEmpty()) {
            errorMessages.addAll(registerResultValidCheck.getErrorMessages());
        }

        registerResult.setErrorFields(errorFields);
        registerResult.setErrorMessages(errorMessages);

        return registerResult;
    }


    @Transactional
    public void registerUser(String email, String username, String password) {
        Register register = new Register(email, username, password);
        userMapper.registerUser(register);

        userMapper.setLetterConfig(register.getId());
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

        String uuid = Utilities.createCode(36);
        String fileName = multipartFile.getOriginalFilename();
        String extension = filePath.substring(fileName.lastIndexOf(".") + 1);
        String directory = filePath + "/" + uuid + "." + extension;

        ProfileImage profileImage = new ProfileImage();
        profileImage.setUuid(uuid);
        profileImage.setUserId(userId);
        profileImage.setFileName(fileName);
        profileImage.setExtension(extension);
        profileImage.setDirectory(directory);

        return profileImage;
    }

    @Transactional
    public ProfileImage createProfileImage(MultipartFile multipartFile, Long userId) throws IOException {
        ProfileImage profileImage = setProfileImage(multipartFile, userId);

        File file = new File(profileImage.getDirectory());
        multipartFile.transferTo(file);

        userMapper.createProfileImage(profileImage);

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
}
