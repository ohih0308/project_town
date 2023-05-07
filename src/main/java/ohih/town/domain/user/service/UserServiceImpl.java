package ohih.town.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.*;
import ohih.town.domain.VerificationResult;
import ohih.town.domain.user.dto.*;
import ohih.town.domain.user.mapper.UserMapper;
import ohih.town.utilities.Utilities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static ohih.town.constants.ErrorsConst.*;
import static ohih.town.constants.SuccessConst.*;
import static ohih.town.constants.DomainConst.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final JavaMailSender javaMailSender;

    @Value("#{verificationMail['mail.from']}")
    private String from;
    @Value("#{verificationMail['mail.verification.subject']}")
    private String verificationSubject;
    @Value("#{verificationMail['mail.verification.body']}")
    private String verificationBody;

    @Value("#{filePaths['profile.image.directory']}")
    private String profileImageDirectory;


    private final UserMapper userMapper;


    @Override
    public MailSendResult sendVerificationCode(String email, String verificationCode) {
        MailSendResult mailSendResult = MailSendResult.builder()
                .from(from)
                .to(email)
                .resultMessage(userErrorMessageSource.getString(MAIL_VERIFICATION_SENT_FAILURE))
                .build();

        Map<String, String> errorMessages = new HashMap<>();
        boolean isValidated = Utilities.isValidated(ValidationPatterns.EMAIL, email);
        boolean isDuplicated = isDuplicated(DomainConst.EMAIL, email);

        if (!isValidated) {
            errorMessages.put(DomainConst.EMAIL, userErrorMessageSource.getString(ErrorsConst.USER_EMAIL_INVALID));
        }
        if (isDuplicated) {
            errorMessages.put(DomainConst.EMAIL, userErrorMessageSource.getString(ErrorsConst.USER_EMAIL_DUPLICATED));
        }

        if (!isValidated || isDuplicated) {
            mailSendResult.setErrorMessages(errorMessages);
            return mailSendResult;
        }


        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(verificationSubject);
        simpleMailMessage.setText(verificationBody.replace("${verification-code}", verificationCode));

        try {
            javaMailSender.send(simpleMailMessage);
            mailSendResult.setSent(true);
            mailSendResult.setResultMessage(successMessageSource.getString(VERIFICATION_EMAIL_SENT));
        } catch (MailException e) {
            log.info("MailException. email = {}.", email);
        }

        return mailSendResult;
    }

    @Override
    public VerificationResult checkEmailVerificationCode(String EMAIL_VERIFICATION_REQUEST, String EMAIL_VERIFICATION_CODE,
                                                         String email, String verificationCode) {
        VerificationResult verificationResult = new VerificationResult();
        Map<String, String> messages = new HashMap<>();

        if (EMAIL_VERIFICATION_REQUEST == null) {
            messages.put(SessionConst.EMAIL_VERIFICATION_REQUEST, userErrorMessageSource.getString(MAIL_VERIFICATION_EMAIL_NOT_SENT));
        }
        if (EMAIL_VERIFICATION_CODE == null) {
            messages.put(SessionConst.EMAIL_VERIFICATION_CODE, userErrorMessageSource.getString(MAIL_VERIFICATION_CODE_NULL));
        }
        if (email == null) {
            messages.put(DomainConst.EMAIL, userErrorMessageSource.getString(USER_EMAIL_NULL));
        }
        if (verificationCode == null) {
            messages.put(SessionConst.EMAIL_VERIFICATION_CODE, userErrorMessageSource.getString(MAIL_VERIFICATION_CODE_NULL));
        }

        if (!messages.isEmpty()) {
            verificationResult.setMessages(messages);
            return verificationResult;
        }

        if (!EMAIL_VERIFICATION_REQUEST.equals(email)) {
            messages.put(DomainConst.EMAIL, userErrorMessageSource.getString(USER_EMAIL_EMAIL_MISMATCH));
        } else {
            if (EMAIL_VERIFICATION_CODE.equals(verificationCode)) {
                messages.put(SessionConst.EMAIL_VERIFICATION_CODE, successMessageSource.getString(EMAIL_VERIFICATION_SUCCESS));

                verificationResult.setVerified(true);
                verificationResult.setVerifiedValue(email);
            } else {
                messages.put(SessionConst.EMAIL_VERIFICATION_CODE, userErrorMessageSource.getString(MAIL_VERIFICATION_CODE_MISMATCH));
            }
        }

        verificationResult.setMessages(messages);

        return verificationResult;
    }

    @Override
    public boolean isDuplicated(String field, String value) {
        Map<String, String> map = new HashMap<>();
        map.put(UtilityConst.FIELD, field);
        map.put(UtilityConst.VALUE, value);

        return userMapper.isDuplicated(map) != 0;
    }

    @Override
    public VerificationResult verifyEmail(String email) {
        VerificationResult verificationResult = new VerificationResult();
        Map<String, String> messages = new HashMap<>();

        if (email == null) {
            messages.put(DomainConst.EMAIL, userErrorMessageSource.getString(USER_EMAIL_NULL));
        } else {
            boolean isValidated = Utilities.isValidated(ValidationPatterns.EMAIL, email);
            boolean isDuplicated = isDuplicated(EMAIL, email);

            if (!isValidated) {
                messages.put(DomainConst.EMAIL, userErrorMessageSource.getString(USER_EMAIL_INVALID));
            }
            if (isDuplicated) {
                messages.put(DomainConst.EMAIL, userErrorMessageSource.getString(USER_EMAIL_DUPLICATED));
            }

            if (isValidated && !isDuplicated) {
                verificationResult.setVerified(true);
                verificationResult.setVerifiedValue(email);
                messages.put(EMAIL, successMessageSource.getString(USER_EMAIL_VALID));
            }
        }

        verificationResult.setMessages(messages);

        return verificationResult;
    }

    @Override
    public VerificationResult verifyUsername(String username) {
        VerificationResult verificationResult = new VerificationResult();
        Map<String, String> messages = new HashMap<>();

        if (username == null) {
            messages.put(USERNAME, userErrorMessageSource.getString(USER_USERNAME_NULL));
        } else {
            boolean isDuplicated = isDuplicated(USERNAME, username);
            boolean isValidated = Utilities.isValidated(ValidationPatterns.USERNAME, username);

            if (isDuplicated) {
                messages.put(USERNAME, userErrorMessageSource.getString(USER_USERNAME_DUPLICATED));
            }
            if (!isValidated) {
                messages.put(USERNAME, userErrorMessageSource.getString(USER_USERNAME_INVALID));
            }

            if (isValidated && !isDuplicated) {
                verificationResult.setVerified(true);
                verificationResult.setVerifiedValue(username);
                messages.put(USERNAME, successMessageSource.getString(USER_USERNAME_VALID));
            }
        }

        verificationResult.setMessages(messages);

        return verificationResult;
    }

    @Override
    public VerificationResult verifyPassword(String password) {
        VerificationResult verificationResult = new VerificationResult();
        Map<String, String> messages = new HashMap<>();
        verificationResult.setVerified(Utilities.isValidated(ValidationPatterns.PASSWORD, password));

        if (verificationResult.isVerified()) {
            messages.put(DomainConst.PASSWORD, successMessageSource.getString(USER_PASSWORD_VALID));
        } else {
            messages.put(DomainConst.PASSWORD, userErrorMessageSource.getString(USER_PASSWORD_INVALID));
        }

        verificationResult.setMessages(messages);

        return verificationResult;
    }

    @Override
    public VerificationResult verifyPasswordConfirmation(String password, String passwordConfirmation) {
        VerificationResult verificationResult = new VerificationResult();
        Map<String, String> messages = new HashMap<>();

        if (!Utilities.isValidated(ValidationPatterns.PASSWORD, password)) {
            messages.put(PASSWORD, userErrorMessageSource.getString(USER_PASSWORD_INVALID));
        }
        if (!Utilities.isValidated(ValidationPatterns.PASSWORD, passwordConfirmation)) {
            messages.put(PASSWORD_CONFIRMATION, userErrorMessageSource.getString(USER_PASSWORD_CONFIRMATION_INVALID));
        }

        if (!messages.isEmpty()) {
            verificationResult.setMessages(messages);
            return verificationResult;
        }

        verificationResult.setVerified(password.equals(passwordConfirmation));

        if (verificationResult.isVerified()) {
            messages.put(PASSWORD_CONFIRMATION, successMessageSource.getString(USER_PASSWORD_CONFIRMATION_VALID));
        } else {
            messages.put(PASSWORD_CONFIRMATION, userErrorMessageSource.getString(USER_PASSWORD_CONFIRMATION_INVALID));
        }

        verificationResult.setMessages(messages);

        return verificationResult;
    }

    @Override
    public boolean hasNull(RegisterRequest registerRequest) {
        return registerRequest.getEmail() == null ||
                registerRequest.getUsername() == null ||
                registerRequest.getPassword() == null ||
                registerRequest.getPasswordConfirmation() == null;
    }

    @Override
    public VerificationResult verifyRegisterRequest(RegisterRequest registerRequest, String verifiedEmail) {
        VerificationResult verificationResult = new VerificationResult();
        Map<String, String> messages = new HashMap<>();

        if (hasNull(registerRequest)) {
            messages.put(REGISTER_REQUEST, userErrorMessageSource.getString(USER_REGISTER_REQUEST_NULL));
        }
        if (verifiedEmail == null) {
            messages.put(SessionConst.VERIFIED_EMAIL, userErrorMessageSource.getString(MAIL_VERIFICATION_REQUEST_NOTFOUND));
        }

        if (!messages.isEmpty()) {
            verificationResult.setMessages(messages);
            return verificationResult;
        }

        if (!verifiedEmail.equals(registerRequest.getEmail())) {
            messages.put(EMAIL, userErrorMessageSource.getString(MAIL_VERIFICATION_EMAIL_MISMATCH));
        }

        VerificationResult emailVerification = verifyEmail(registerRequest.getEmail());
        VerificationResult usernameVerification = verifyUsername(registerRequest.getUsername());
        VerificationResult passwordVerification = verifyPassword(registerRequest.getPassword());
        VerificationResult confirmationVerification = verifyPasswordConfirmation(registerRequest.getPassword(),
                registerRequest.getPasswordConfirmation());

        if (!emailVerification.isVerified()) {
            messages.putAll(emailVerification.getMessages());
        }
        if (!usernameVerification.isVerified()) {
            messages.putAll(usernameVerification.getMessages());
        }
        if (!passwordVerification.isVerified()) {
            messages.putAll(passwordVerification.getMessages());
        }
        if (!confirmationVerification.isVerified()) {
            messages.putAll(confirmationVerification.getMessages());
        }
        if (!registerRequest.isAgreement()) {
            messages.put(DomainConst.AGREEMENT, userErrorMessageSource.getString(USER_AGREEMENT_MISSING));
        }


        if (messages.isEmpty()) {
            verificationResult.setVerified(true);
        }

        verificationResult.setMessages(messages);

        return verificationResult;
    }

    @Override
    @Transactional
    public RegisterResult registerUser(RegisterRequest registerRequest, String verifiedEmail) {
        RegisterResult registerResult = new RegisterResult();
        VerificationResult verificationResult = verifyRegisterRequest(registerRequest, verifiedEmail);

        if (!verificationResult.isVerified()) {
            registerResult.setErrorMessages(verificationResult.getMessages());
            registerResult.setResultMessage(userErrorMessageSource.getString(USER_REGISTER_FAILURE));
            return registerResult;
        }

        boolean isRegistered = userMapper.registerUser(registerRequest);
        boolean isInitialized = userMapper.initGuestbookConfigs(registerRequest.getUserId());

        try {
            if (isRegistered || isInitialized) {
                registerResult.setRegistered(true);
                registerResult.setResultMessage(successMessageSource.getString(USER_REGISTER_SUCCESS));
                registerResult.setRedirectUrl(URLConst.HOME);
            } else {
                registerResult.setResultMessage(userErrorMessageSource.getString(USER_REGISTER_FAILURE));
                registerResult.setRedirectUrl(URLConst.REGISTER);
                throw new RuntimeException();
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info("{}", e.getMessage());
        }

        return registerResult;
    }

    @Override
    public LoginResult login(String email, String password) {
        LoginResult loginResult = new LoginResult();
        Map<String, String> messages = new HashMap<>();

        VerificationResult emailVerification = verifyEmail(email);
        VerificationResult passwordVerification = verifyPassword(password);

        if (!emailVerification.isVerified()) {
            messages.putAll(emailVerification.getMessages());
        }
        if (!passwordVerification.isVerified()) {
            messages.putAll(passwordVerification.getMessages());
        }

        if (!messages.isEmpty()) {
            loginResult.setErrorMessages(messages);
            loginResult.setResultMessage(userErrorMessageSource.getString(USER_LOGIN_FAILURE_INVALID_CREDENTIALS));
            return loginResult;
        }

        Map<String, String> map = new HashMap<>();
        map.put(EMAIL, email);
        map.put(PASSWORD, password);

        UserInfo userInfo = userMapper.login(map);

        if (userInfo == null) {
            loginResult.setResultMessage(userErrorMessageSource.getString(USER_LOGIN_FAILURE_INVALID_CREDENTIALS));
        } else {
            loginResult.setLoggedIn(true);
            loginResult.setResultMessage(successMessageSource.getString(USER_LOGIN_SUCCESS));
            loginResult.setUserInfo(userInfo);
            loginResult.setResultMessage(URLConst.HOME);
        }

        return loginResult;
    }

    @Override
    public ProfileImage extractProfileImageFromRequest(MultipartFile multipartFile, Long userId) {
        return ProfileImage.builder()
                .userId(userId)
                .originalFileName(multipartFile.getOriginalFilename())
                .extension(Utilities.extractExtension(Objects.requireNonNull(multipartFile.getOriginalFilename())))
                .directory(profileImageDirectory + multipartFile.getOriginalFilename())
                .build();
    }

    @Override
    @Transactional
    public ProfileImageResult uploadProfileImage(MultipartFile multipartFile, Long userId) {
        ProfileImageResult profileImageResult = new ProfileImageResult();
        ProfileImage profileImage = extractProfileImageFromRequest(multipartFile, userId);

        if (userMapper.uploadProfileImage(profileImage)) {
            try {
                File file = new File(profileImage.getDirectory());
                multipartFile.transferTo(file);

                profileImageResult.setSuccess(true);
                profileImageResult.setResultMessage(successMessageSource.getString(UPLOAD_PROFILE_IMAGE_SUCCESS));
                profileImageResult.setProfileImageDirectory(profileImage.getDirectory());
            } catch (IOException e) {
                log.info("{}", e.getMessage());
                profileImageResult.setResultMessage(userErrorMessageSource.getString(USER_PROFILE_IMAGE_UPLOAD_FAILURE));
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        } else {
            profileImageResult.setResultMessage(userErrorMessageSource.getString(USER_PROFILE_IMAGE_UPLOAD_FAILURE));
        }

        return profileImageResult;
    }

    @Override
    @Transactional
    public ProfileImageResult updateProfileImage(MultipartFile multipartFile, Long userId, String oldProfileImageDirectory) {
        ProfileImageResult profileImageResult = new ProfileImageResult();
        ProfileImage newProfileImage = extractProfileImageFromRequest(multipartFile, userId);

        if (userMapper.updateProfileImage(newProfileImage)) {
            try {
                multipartFile.transferTo(new File(newProfileImage.getDirectory()));

                File oldProfileImage = new File(oldProfileImageDirectory);
                if (!oldProfileImage.delete()) {
                    throw new IOException();
                }
                profileImageResult.setSuccess(true);
                profileImageResult.setResultMessage(successMessageSource.getString(UPDATE_PROFILE_IMAGE_SUCCESS));
                profileImageResult.setProfileImageDirectory(newProfileImage.getDirectory());
            } catch (IOException e) {
                log.info("{}", e.getMessage());
                profileImageResult.setResultMessage(userErrorMessageSource.getString(USER_PROFILE_IMAGE_UPDATE_FAILURE));
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        } else {
            profileImageResult.setResultMessage(userErrorMessageSource.getString(USER_PROFILE_IMAGE_UPDATE_FAILURE));
        }

        return profileImageResult;
    }

    @Override
    @Transactional
    public ProfileImageResult deleteProfileImage(String directory) {
        ProfileImageResult profileImageResult = new ProfileImageResult();

        if (userMapper.deleteProfileImage(directory)) {
            try {
                File file = new File(directory);
                if (!file.delete()) {
                    throw new IOException();
                }
                profileImageResult.setSuccess(true);
                profileImageResult.setResultMessage(successMessageSource.getString(DELETE_PROFILE_IMAGE_SUCCESS));
                profileImageResult.setProfileImageDirectory(null);
            } catch (IOException e) {
                log.info("{}", e.getMessage());
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                profileImageResult.setResultMessage(userErrorMessageSource.getString(USER_PROFILE_IMAGE_DELETE_FAILURE));
            }
        } else {
            profileImageResult.setResultMessage(userErrorMessageSource.getString(USER_PROFILE_IMAGE_DELETE_FAILURE));
        }

        return profileImageResult;
    }

    @Override
    public UserInfoUpdateResult updateUsername(Long userId, String username) {
        UserInfoUpdateResult userInfoUpdateResult = new UserInfoUpdateResult();
        VerificationResult verificationResult = verifyUsername(username);

        if (!verificationResult.isVerified()) {
            userInfoUpdateResult.setErrorMessages(verificationResult.getMessages());
            return userInfoUpdateResult;
        }

        Map<String, Object> map = new HashMap<>();
        map.put(UtilityConst.FIELD, USERNAME);
        map.put(UtilityConst.VALUE, username);
        map.put(USER_ID, userId);

        if (userMapper.updateUserInfo(map)) {
            userInfoUpdateResult.setSuccess(true);
            userInfoUpdateResult.setResultMessage(successMessageSource.getString(USER_UPDATE_USERNAME_SUCCESS));
        } else {
            userInfoUpdateResult.setResultMessage(userErrorMessageSource.getString(USER_UPDATE_USERNAME_FAILURE));
        }

        return userInfoUpdateResult;
    }

    @Override
    public UserInfoUpdateResult updatePassword(Long userId, String password) {
        UserInfoUpdateResult userInfoUpdateResult = new UserInfoUpdateResult();
        VerificationResult verificationResult = verifyPassword(password);

        if (!verificationResult.isVerified()) {
            userInfoUpdateResult.setErrorMessages(verificationResult.getMessages());
            return userInfoUpdateResult;
        }

        Map<String, Object> map = new HashMap<>();
        map.put(UtilityConst.FIELD, PASSWORD);
        map.put(UtilityConst.VALUE, password);
        map.put(USER_ID, userId);

        if (userMapper.updateUserInfo(map)) {
            userInfoUpdateResult.setSuccess(true);
            userInfoUpdateResult.setResultMessage(successMessageSource.getString(USER_PASSWORD_UPDATE_SUCCESS));
        } else {
            userInfoUpdateResult.setResultMessage(userErrorMessageSource.getString(USER_UPDATE_PASSWORD_FAILURE));
        }

        return userInfoUpdateResult;
    }

    /*
    * deactivate user account
    * */

    @Override
    public UserInfoUpdateResult updateGuestbookPermission(GuestbookPermission guestbookPermission) {
        UserInfoUpdateResult userInfoUpdateResult = new UserInfoUpdateResult();

        if (userMapper.updateGuestbookPermission(guestbookPermission)) {
            userInfoUpdateResult.setSuccess(true);
            userInfoUpdateResult.setResultMessage(successMessageSource.getString(GUESTBOOK_PERMISSION_UPDATE_SUCCESS));
        } else {
            userInfoUpdateResult.setResultMessage(userErrorMessageSource.getString(USER_GUESTBOOK_PERMISSION_UPDATE_FAILURE));
        }

        return userInfoUpdateResult;
    }

    @Override
    public UserInfoUpdateResult updateGuestbookActivation(Long userId, boolean activation) {
//        Map<String, Object> map = new HashMap<>();
//        map.put(GUESTBOOK_ACTIVATION, activation);
//        map.put(USER_ID, userId);
//
//        if ()
        return null;
    }
}
