package ohih.town.domain.mail.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.SessionConst;
import ohih.town.constants.URLConst;
import ohih.town.constants.UserConst;
import ohih.town.constants.ValidationPatterns;
import ohih.town.domain.mail.dto.EmailVerificationResult;
import ohih.town.domain.mail.dto.MailResult;
import ohih.town.domain.mail.service.MailService;
import ohih.town.domain.common.dto.FieldValidation;
import ohih.town.session.SessionManager;
import ohih.town.utilities.Utilities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ResourceBundle;

import static ohih.town.constants.ErrorMessageResourceBundle.USER_ERROR_MESSAGES;
import static ohih.town.constants.ErrorsConst.*;
import static ohih.town.constants.SessionConst.EMAIL_VERIFICATION_CODE;
import static ohih.town.constants.SessionConst.EMAIL_VERIFICATION_REQUEST;
import static ohih.town.constants.SuccessConst.USER_EMAIL_VALID;
import static ohih.town.constants.UserConst.VERIFICATION_CODE_LENGTH;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MailRestController {
    @Value("#{verificationMail['mail.from']}")
    private String from;

    private final ResourceBundle userErrorMessageSource = USER_ERROR_MESSAGES;

    private final UserServiceImpl2 userServiceImpl2;
    private final MailService mailService;


    @PostMapping(URLConst.SEND_VERIFICATION_CODE)
    public MailResult sendVerificationCode(HttpServletRequest request, String email) {
        MailResult mailResult = new MailResult(from, email);
        FieldValidation fieldValidation = userServiceImpl2.checkValidationAndDuplication(ValidationPatterns.EMAIL,
                USER_EMAIL_INVALID, USER_EMAIL_DUPLICATED,
                USER_EMAIL_VALID,
                UserConst.EMAIL, email);

        if (fieldValidation.isValid() && fieldValidation.isDuplicated()) {
            String verificationCode = Utilities.createCode(VERIFICATION_CODE_LENGTH);

            mailService.sendVerificationCode(mailResult, email, verificationCode);

            log.info("email = {}, verification code = {}", email, verificationCode);
            SessionManager.setAttributes(request, EMAIL_VERIFICATION_CODE, verificationCode);
            SessionManager.setAttributes(request, EMAIL_VERIFICATION_REQUEST, email);
        } else {
            mailResult.setResultMessage(userErrorMessageSource.getString(MAIL_VERIFICATION_SENT_FAILURE));
            mailResult.setErrorMessages(fieldValidation.getMessages());
        }

        return mailResult;
    }

    @PostMapping(URLConst.VERIFY_EMAIL_CODE)
    public EmailVerificationResult verifyEmailCode(HttpServletRequest request,
                                                   String email, String verificationCode) {

        String EMAIL_VERIFICATION_REQUEST = (String) SessionManager.getAttributes(request, SessionConst.EMAIL_VERIFICATION_REQUEST);
        String EMAIL_VERIFICATION_CODE = (String) SessionManager.getAttributes(request, SessionConst.EMAIL_VERIFICATION_CODE);

        EmailVerificationResult emailVerificationResult = mailService.verifyEmailCode(EMAIL_VERIFICATION_REQUEST, EMAIL_VERIFICATION_CODE, email, verificationCode);

        if (emailVerificationResult.getSuccess()) {
            SessionManager.setAttributes(request, SessionConst.VALIDATED_EMAIL, email);
            SessionManager.removeAttribute(request, EMAIL_VERIFICATION_CODE);
            SessionManager.removeAttribute(request, EMAIL_VERIFICATION_REQUEST);
        }

        return emailVerificationResult;
    }
}
