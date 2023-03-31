package ohih.town.domain.mail.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ohih.town.constants.SessionConst;
import ohih.town.constants.URLConst;
import ohih.town.constants.UserConst;
import ohih.town.constants.ValidationPatterns;
import ohih.town.domain.mail.EmailVerificationRequest;
import ohih.town.domain.mail.EmailVerificationResult;
import ohih.town.domain.mail.MailProperties;
import ohih.town.domain.mail.MailResult;
import ohih.town.domain.mail.service.MailService;
import ohih.town.domain.user.dto.CheckResult;
import ohih.town.domain.user.service.UserService;
import ohih.town.session.SessionManager;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static ohih.town.constants.ErrorMessagesResourceBundle.MAIL_ERROR_MESSAGES;
import static ohih.town.constants.ErrorMessagesResourceBundle.USER_ERROR_MESSAGES;
import static ohih.town.constants.ErrorsConst.*;
import static ohih.town.constants.SessionConst.EMAIL_VERIFICATION_REQUEST;
import static ohih.town.constants.SessionConst.VALIDATED_EMAIL;
import static ohih.town.constants.SuccessConst.USER_EMAIL_VALID;
import static ohih.town.constants.SuccessMessagesResourceBundle.SUCCESS_MESSAGES;
import static ohih.town.session.SessionManager.getAttributes;

@RestController
@RequiredArgsConstructor
public class MailRestController {

    private final UserService userService;
    private final MailService mailService;
    private MailProperties mailProperties = new MailProperties();


    @PostMapping(URLConst.SEND_VERIFICATION_CODE)
    public MailResult sendVerificationCode(HttpServletRequest request, String email) {
        MailResult mailResult = new MailResult();

        CheckResult checkResult = userService.checkValidationAndDuplication(ValidationPatterns.EMAIL,
                USER_ERROR_MESSAGES, SUCCESS_MESSAGES,
                USER_EMAIL_INVALID, USER_EMAIL_DUPLICATED,
                USER_EMAIL_VALID,
                UserConst.EMAIL, email);

        if (checkResult.getIsValid() && !checkResult.getIsDuplicated()) {
            try {
                EmailVerificationRequest emailVerificationRequest = mailService.sendVerificationCode(email);

                SessionManager.setAttributes(request, EMAIL_VERIFICATION_REQUEST, emailVerificationRequest);

                mailResult.setFrom(mailProperties.getFrom());
                mailResult.setTo(email);
                mailResult.setIsSent(true);
            } catch (MailException e) {
                mailResult.setFrom(mailProperties.getFrom());
                mailResult.setTo(email);
                mailResult.setIsSent(false);

                String errorMessage = MAIL_ERROR_MESSAGES.getString(MAIL_SEND_ERROR);
                mailResult.setErrorMessage(errorMessage);
            }

            SessionManager.setAttributes(request, SessionConst.VALIDATED_EMAIL, email);
        } else {
            mailResult.setFrom(mailProperties.getFrom());
            mailResult.setTo(email);
            mailResult.setIsSent(false);

            mailResult.setErrorMessage(checkResult.getMessage());
        }

        return mailResult;
    }

    @PostMapping(URLConst.VERIFY_EMAIL_CODE)
    public EmailVerificationResult verifyEmailCode(HttpServletRequest request,
                                                   String emailVerificationCode) {
        EmailVerificationRequest EMAIL_VERIFICATION_REQUEST = (EmailVerificationRequest) SessionManager.getAttributes(request, SessionConst.EMAIL_VERIFICATION_REQUEST);
        EmailVerificationResult emailVerificationResult = mailService.verifyEmailCode(EMAIL_VERIFICATION_REQUEST, emailVerificationCode);

        if (emailVerificationResult.getSuccess()) {
            SessionManager.setAttributes(request, SessionConst.AUTHENTICATED_EMAIL, EMAIL_VERIFICATION_REQUEST.getEmail());
            SessionManager.removeAttribute(request, SessionConst.EMAIL_VERIFICATION_REQUEST);
        } else {
            SessionManager.removeAttribute(request, VALIDATED_EMAIL);
        }

        return emailVerificationResult;
    }
}
