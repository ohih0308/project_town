package ohih.town.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.*;
import ohih.town.domain.VerificationResult;
import ohih.town.domain.comment.mapper.CommentMapper;
import ohih.town.domain.guestbook.mapper.GuestbookMapper;
import ohih.town.domain.post.mapper.PostMapper;
import ohih.town.domain.user.dto.*;
import ohih.town.domain.user.mapper.UserMapper;
import ohih.town.utilities.Search;
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
import java.util.ResourceBundle;

import static ohih.town.constants.DomainConst.*;
import static ohih.town.constants.ErrorConst.*;
import static ohih.town.constants.ResourceBundleConst.*;
import static ohih.town.constants.SuccessConst.*;

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
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final GuestbookMapper guestbookMapper;


    @Override
    public MailSendResult sendVerificationCode(String email, String verificationCode) {
        MailSendResult mailSendResult = MailSendResult.builder()
                .from(from)
                .to(email)
                .resultMessage(USER_ERROR_MESSAGES.getString(MAIL_VERIFICATION_SENT_FAILURE))
                .build();

        Map<String, String> errorMessages = new HashMap<>();
        boolean isValidated = Utilities.isValidated(ValidationPatterns.EMAIL, email);
        boolean isDuplicated = isDuplicated(DomainConst.EMAIL, email);

        if (!isValidated) {
            errorMessages.put(DomainConst.EMAIL, USER_ERROR_MESSAGES.getString(ErrorConst.USER_EMAIL_INVALID));
        }
        if (isDuplicated) {
            errorMessages.put(DomainConst.EMAIL, USER_ERROR_MESSAGES.getString(ErrorConst.USER_EMAIL_DUPLICATED));
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
            mailSendResult.setResultMessage(SUCCESS_MESSAGES.getString(VERIFICATION_EMAIL_SENT));
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
            messages.put(SessionConst.EMAIL_VERIFICATION_REQUEST, USER_ERROR_MESSAGES.getString(MAIL_VERIFICATION_EMAIL_NOT_SENT));
        }
        if (EMAIL_VERIFICATION_CODE == null) {
            messages.put(SessionConst.EMAIL_VERIFICATION_CODE, USER_ERROR_MESSAGES.getString(MAIL_VERIFICATION_CODE_NULL));
        }
        if (email == null) {
            messages.put(DomainConst.EMAIL, USER_ERROR_MESSAGES.getString(USER_EMAIL_NULL));
        }
        if (verificationCode == null) {
            messages.put(SessionConst.EMAIL_VERIFICATION_CODE, USER_ERROR_MESSAGES.getString(MAIL_VERIFICATION_CODE_NULL));
        }

        if (!messages.isEmpty()) {
            verificationResult.setMessages(messages);
            return verificationResult;
        }

        if (!EMAIL_VERIFICATION_REQUEST.equals(email)) {
            messages.put(DomainConst.EMAIL, USER_ERROR_MESSAGES.getString(USER_EMAIL_EMAIL_MISMATCH));
        } else {
            if (EMAIL_VERIFICATION_CODE.equals(verificationCode)) {
                messages.put(SessionConst.EMAIL_VERIFICATION_CODE, SUCCESS_MESSAGES.getString(EMAIL_VERIFICATION_SUCCESS));

                verificationResult.setVerified(true);
                verificationResult.setVerifiedValue(email);
            } else {
                messages.put(SessionConst.EMAIL_VERIFICATION_CODE, USER_ERROR_MESSAGES.getString(MAIL_VERIFICATION_CODE_MISMATCH));
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
            messages.put(DomainConst.EMAIL, USER_ERROR_MESSAGES.getString(USER_EMAIL_NULL));
        } else {
            boolean isValidated = Utilities.isValidated(ValidationPatterns.EMAIL, email);
            boolean isDuplicated = isDuplicated(EMAIL, email);

            if (!isValidated) {
                messages.put(DomainConst.EMAIL, USER_ERROR_MESSAGES.getString(USER_EMAIL_INVALID));
            }
            if (isDuplicated) {
                messages.put(DomainConst.EMAIL, USER_ERROR_MESSAGES.getString(USER_EMAIL_DUPLICATED));
            }

            if (isValidated && !isDuplicated) {
                verificationResult.setVerified(true);
                verificationResult.setVerifiedValue(email);
                messages.put(EMAIL, SUCCESS_MESSAGES.getString(USER_EMAIL_VALID));
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
            messages.put(USERNAME, USER_ERROR_MESSAGES.getString(USER_USERNAME_NULL));
        } else {
            boolean isDuplicated = isDuplicated(USERNAME, username);
            boolean isValidated = Utilities.isValidated(ValidationPatterns.USERNAME, username);

            if (isDuplicated) {
                messages.put(USERNAME, USER_ERROR_MESSAGES.getString(USER_USERNAME_DUPLICATED));
            }
            if (!isValidated) {
                messages.put(USERNAME, USER_ERROR_MESSAGES.getString(USER_USERNAME_INVALID));
            }

            if (isValidated && !isDuplicated) {
                verificationResult.setVerified(true);
                verificationResult.setVerifiedValue(username);
                messages.put(USERNAME, SUCCESS_MESSAGES.getString(USER_USERNAME_VALID));
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

        if (password == null) {
            messages.put(DomainConst.PASSWORD, USER_ERROR_MESSAGES.getString(USER_PASSWORD_NULL));
        } else {
            if (verificationResult.isVerified()) {
                messages.put(DomainConst.PASSWORD, SUCCESS_MESSAGES.getString(USER_PASSWORD_VALID));
            } else {
                messages.put(DomainConst.PASSWORD, USER_ERROR_MESSAGES.getString(USER_PASSWORD_INVALID));
            }
        }

        verificationResult.setMessages(messages);

        return verificationResult;
    }

    @Override
    public VerificationResult verifyPasswordConfirmation(String password, String passwordConfirmation) {
        VerificationResult verificationResult = new VerificationResult();
        Map<String, String> messages = new HashMap<>();

        if (!Utilities.isValidated(ValidationPatterns.PASSWORD, password)) {
            messages.put(PASSWORD, USER_ERROR_MESSAGES.getString(USER_PASSWORD_INVALID));
        }
        if (!Utilities.isValidated(ValidationPatterns.PASSWORD, passwordConfirmation)) {
            messages.put(PASSWORD_CONFIRMATION, USER_ERROR_MESSAGES.getString(USER_PASSWORD_CONFIRMATION_INVALID));
        }

        if (!messages.isEmpty()) {
            verificationResult.setMessages(messages);
            return verificationResult;
        }

        verificationResult.setVerified(password.equals(passwordConfirmation));

        if (verificationResult.isVerified()) {
            messages.put(PASSWORD_CONFIRMATION, SUCCESS_MESSAGES.getString(USER_PASSWORD_CONFIRMATION_VALID));
        } else {
            messages.put(PASSWORD_CONFIRMATION, USER_ERROR_MESSAGES.getString(USER_PASSWORD_CONFIRMATION_INVALID));
        }

        verificationResult.setMessages(messages);

        return verificationResult;
    }

    @Override
    public VerificationResult verifyRegisterRequest(RegisterRequest registerRequest, String verifiedEmail) {
        VerificationResult verificationResult = new VerificationResult();
        Map<String, String> messages = new HashMap<>();

        if (hasNull(registerRequest)) {
            messages.put(REGISTER_REQUEST, USER_ERROR_MESSAGES.getString(USER_REGISTER_REQUEST_NULL));
        }
        if (verifiedEmail == null) {
            messages.put(SessionConst.VERIFIED_EMAIL, USER_ERROR_MESSAGES.getString(MAIL_VERIFICATION_REQUEST_NOTFOUND));
        }

        if (!messages.isEmpty()) {
            verificationResult.setMessages(messages);
            return verificationResult;
        }

        if (!verifiedEmail.equals(registerRequest.getEmail())) {
            messages.put(EMAIL, USER_ERROR_MESSAGES.getString(MAIL_VERIFICATION_EMAIL_MISMATCH));
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
            messages.put(DomainConst.AGREEMENT, USER_ERROR_MESSAGES.getString(USER_AGREEMENT_MISSING));
        }


        if (messages.isEmpty()) {
            verificationResult.setVerified(true);
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
    @Transactional
    public RegisterResult registerUser(RegisterRequest registerRequest, String verifiedEmail) {
        RegisterResult registerResult = new RegisterResult();
        VerificationResult verificationResult = verifyRegisterRequest(registerRequest, verifiedEmail);

        if (!verificationResult.isVerified()) {
            registerResult.setErrorMessages(verificationResult.getMessages());
            registerResult.setResultMessage(USER_ERROR_MESSAGES.getString(USER_REGISTER_FAILURE));
            return registerResult;
        }

        boolean isRegistered = userMapper.registerUser(registerRequest);
        boolean isInitialized = userMapper.initGuestbookConfigs(registerRequest.getUserId());

        try {
            if (isRegistered || isInitialized) {
                registerResult.setRegistered(true);
                registerResult.setResultMessage(SUCCESS_MESSAGES.getString(USER_REGISTER_SUCCESS));
                registerResult.setRedirectUrl(URLConst.HOME);
            } else {
                registerResult.setResultMessage(USER_ERROR_MESSAGES.getString(USER_REGISTER_FAILURE));
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
            loginResult.setResultMessage(USER_ERROR_MESSAGES.getString(USER_LOGIN_FAILURE_INVALID_CREDENTIALS));
            return loginResult;
        }

        Map<String, String> map = new HashMap<>();
        map.put(EMAIL, email);
        map.put(PASSWORD, password);

        UserInfo userInfo = userMapper.login(map);

        if (userInfo == null) {
            loginResult.setResultMessage(USER_ERROR_MESSAGES.getString(USER_LOGIN_FAILURE_INVALID_CREDENTIALS));
        } else {
            loginResult.setLoggedIn(true);
            loginResult.setResultMessage(SUCCESS_MESSAGES.getString(USER_LOGIN_SUCCESS));
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
                profileImageResult.setResultMessage(SUCCESS_MESSAGES.getString(UPLOAD_PROFILE_IMAGE_SUCCESS));
                profileImageResult.setProfileImageDirectory(profileImage.getDirectory());
            } catch (IOException e) {
                log.info("{}", e.getMessage());
                profileImageResult.setResultMessage(USER_ERROR_MESSAGES.getString(USER_PROFILE_IMAGE_UPLOAD_FAILURE));
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        } else {
            profileImageResult.setResultMessage(USER_ERROR_MESSAGES.getString(USER_PROFILE_IMAGE_UPLOAD_FAILURE));
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
                profileImageResult.setResultMessage(SUCCESS_MESSAGES.getString(UPDATE_PROFILE_IMAGE_SUCCESS));
                profileImageResult.setProfileImageDirectory(newProfileImage.getDirectory());
            } catch (IOException e) {
                log.info("{}", e.getMessage());
                profileImageResult.setResultMessage(USER_ERROR_MESSAGES.getString(USER_PROFILE_IMAGE_UPDATE_FAILURE));
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        } else {
            profileImageResult.setResultMessage(USER_ERROR_MESSAGES.getString(USER_PROFILE_IMAGE_UPDATE_FAILURE));
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
                profileImageResult.setResultMessage(SUCCESS_MESSAGES.getString(DELETE_PROFILE_IMAGE_SUCCESS));
                profileImageResult.setProfileImageDirectory(null);
            } catch (IOException e) {
                log.info("{}", e.getMessage());
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                profileImageResult.setResultMessage(USER_ERROR_MESSAGES.getString(USER_PROFILE_IMAGE_DELETE_FAILURE));
            }
        } else {
            profileImageResult.setResultMessage(USER_ERROR_MESSAGES.getString(USER_PROFILE_IMAGE_DELETE_FAILURE));
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
            userInfoUpdateResult.setResultMessage(SUCCESS_MESSAGES.getString(USER_UPDATE_USERNAME_SUCCESS));
        } else {
            userInfoUpdateResult.setResultMessage(USER_ERROR_MESSAGES.getString(USER_UPDATE_USERNAME_FAILURE));
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
            userInfoUpdateResult.setResultMessage(SUCCESS_MESSAGES.getString(USER_PASSWORD_UPDATE_SUCCESS));
        } else {
            userInfoUpdateResult.setResultMessage(USER_ERROR_MESSAGES.getString(USER_UPDATE_PASSWORD_FAILURE));
        }

        return userInfoUpdateResult;
    }

    @Override
    public UserInfoUpdateResult updateGuestbookPermission(GuestbookPermission guestbookPermission) {
        UserInfoUpdateResult userInfoUpdateResult = new UserInfoUpdateResult();

        if (userMapper.updateGuestbookPermission(guestbookPermission)) {
            userInfoUpdateResult.setSuccess(true);
            userInfoUpdateResult.setResultMessage(SUCCESS_MESSAGES.getString(GUESTBOOK_PERMISSION_UPDATE_SUCCESS));
        } else {
            userInfoUpdateResult.setResultMessage(USER_ERROR_MESSAGES.getString(USER_GUESTBOOK_PERMISSION_UPDATE_FAILURE));
        }

        return userInfoUpdateResult;
    }

    @Override
    public UserInfoUpdateResult updateGuestbookActivation(Long userId, boolean isActivated) {
        UserInfoUpdateResult userInfoUpdateResult = new UserInfoUpdateResult();

        Map<String, Object> map = new HashMap<>();
        map.put(USER_ID, userId);
        map.put(IS_ACTIVATED, isActivated);

        if (userMapper.updateGuestbookActivation(map)) {
            userInfoUpdateResult.setSuccess(true);
            userInfoUpdateResult.setResultMessage(SUCCESS_MESSAGES.getString(GUESTBOOK_ACTIVATION_UPDATE_SUCCESS));
        } else {
            userInfoUpdateResult.setResultMessage(GUESTBOOK_ERROR_MESSAGES.getString(GUESTBOOK_ACTIVATION_UPDATE_FAILURE));
        }
        return userInfoUpdateResult;
    }


    @Override
    @Transactional
    public UserInfoUpdateResult deactivateAccount(Long userId, String directory) {
        UserInfoUpdateResult userInfoUpdateResult = new UserInfoUpdateResult();

        try {
            if (!deactivatePosts(userId) ||
                    !deactivateComments(userId) ||
                    !deactivateGuestbookPosts(userId) ||
                    !deactivateGuestbookComments(userId)) {
                throw new Exception();
            }

            UserInfoUpdateResult updateGuestbookActivation = updateGuestbookActivation(userId, false);
            ProfileImageResult profileImageResult = deleteProfileImage(directory);

            if (!updateGuestbookActivation.isSuccess() ||
                    !profileImageResult.isSuccess()) {
                throw new Exception();
            }

            userInfoUpdateResult.setSuccess(true);
            userInfoUpdateResult.setResultMessage(SUCCESS_MESSAGES.getString(USER_DEACTIVATE_SUCCESS));

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            userInfoUpdateResult.setResultMessage(USER_ERROR_MESSAGES.getString(USER_DEACTIVATE_ACCOUNT_FAILURE));
        }

        return userInfoUpdateResult;
    }

    @Override
    public boolean deactivatePosts(Long userId) {
        Map<String, Object> map = new HashMap<>();
        map.put(USER_ID, userId);
        map.put(UtilityConst.SEARCH, new Search());

        Integer totalCount = postMapper.countMyPosts(map).intValue();
        map.clear();
        try {
            map.put(TableNameConst.TABLE_NAME, TableNameConst.POSTS);
            map.put(USER_ID, userId);
            map.put(USER_TYPE, DEACTIVATED_USER);

            if (!Objects.equals(userMapper.updateUserTypeDeactivated(map), totalCount)) {
                throw new Exception();
            }
            return true;
        } catch (Exception e) {
            log.info("{}", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deactivateComments(Long userId) {
        Map<String, Object> map = new HashMap<>();
        map.put(USER_ID, userId);
        map.put(UtilityConst.SEARCH, new Search());

        Integer totalCount = commentMapper.countMyComments(map).intValue();
        map.clear();
        try {
            map.put(TableNameConst.TABLE_NAME, TableNameConst.COMMENTS);
            map.put(USER_ID, userId);
            map.put(USER_TYPE, DEACTIVATED_USER);

            if (!Objects.equals(userMapper.updateUserTypeDeactivated(map), totalCount)) {
                throw new Exception();
            }
            return true;
        } catch (Exception e) {
            log.info("{}", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deactivateGuestbookPosts(Long userId) {
        Map<String, Object> map = new HashMap<>();
        map.put(USER_ID, userId);
        map.put(UtilityConst.SEARCH, new Search());

        Integer totalCount = guestbookMapper.countPosts(map).intValue();
        map.clear();
        try {
            map.put(TableNameConst.TABLE_NAME, TableNameConst.GUESTBOOK_POSTS);
            map.put(USER_ID, userId);
            map.put(USER_TYPE, DEACTIVATED_USER);

            if (!Objects.equals(userMapper.updateUserTypeDeactivated(map), totalCount)) {
                throw new Exception();
            }
            return true;
        } catch (Exception e) {
            log.info("{}", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deactivateGuestbookComments(Long userId) {
        Integer totalCount = userMapper.countGuestbookComment(userId);
        try {
            Map<String, Object> map = new HashMap<>();
            map.put(TableNameConst.TABLE_NAME, TableNameConst.GUESTBOOK_COMMENTS);
            map.put(USER_ID, userId);
            map.put(USER_TYPE, DEACTIVATED_USER);

            if (!Objects.equals(userMapper.updateUserTypeDeactivated(map), totalCount)) {
                throw new Exception();
            }
            return true;
        } catch (Exception e) {
            log.info("{}", e.getMessage());
        }
        return false;
    }
}
