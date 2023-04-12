package ohih.town.domain.mail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.domain.mail.dto.EmailVerificationResult;
import ohih.town.domain.mail.dto.MailResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static ohih.town.constants.ErrorMessageResourceBundle.*;
import static ohih.town.constants.ErrorsConst.*;
import static ohih.town.constants.SuccessConst.EMAIL_VERIFICATION_SUCCESS;
import static ohih.town.constants.SuccessConst.VERIFICATION_EMAIL_SENT;
import static ohih.town.constants.SuccessMessagesResourceBundle.SUCCESS_MESSAGES;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final JavaMailSender javaMailSender;

    private final ResourceBundle mailMessageSource = MAIL_ERROR_MESSAGES;
    private final ResourceBundle successMessageSource = SUCCESS_MESSAGES;
    private final ResourceBundle userErrorMessageSource = USER_ERROR_MESSAGES;
    private final ResourceBundle commonErrorMessageSource = COMMON_ERROR_MESSAGES;


    @Value("#{verificationMail['mail.verification.subject']}")
    private String verificationSubject;
    @Value("#{verificationMail['mail.verification.body']}")
    private String verificationBody;

    public void sendVerificationCode(MailResult mailResult, String email, String verificationCode) {
        mailResult.setTo(email);

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(mailResult.getFrom());
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(verificationSubject);
        simpleMailMessage.setText(verificationBody.replace("${verification-code}", verificationCode));

        try {
            javaMailSender.send(simpleMailMessage);
            mailResult.setSuccess(true);
            mailResult.setResultMessage(successMessageSource.getString(VERIFICATION_EMAIL_SENT));
        } catch (MailException e) {
            mailResult.setResultMessage(mailMessageSource.getString(MAIL_VERIFICATION_SENT_FAILURE));
            log.info("{}", e.getMessage());
        }
    }


    public EmailVerificationResult verifyEmailCode(String EMAIL_VERIFICATION_REQUEST,
                                                   String EMAIL_VERIFICATION_CODE,
                                                   String email,
                                                   String verificationCode) {
        EmailVerificationResult emailVerificationResult = new EmailVerificationResult();
        List<String> errorMessages = new ArrayList<>();

        if (EMAIL_VERIFICATION_REQUEST == null) {
            errorMessages.add(mailMessageSource.getString(MAIL_VERIFICATION_REQUEST_NOTFOUND));
        } else if (email == null) {
            errorMessages.add(userErrorMessageSource.getString(USER_EMAIL_NULL));
        } else if (!EMAIL_VERIFICATION_REQUEST.equals(email)) {
            errorMessages.add(mailMessageSource.getString(MAIL_VERIFICATION_EMAIL_MISMATCH));
        }

        if (EMAIL_VERIFICATION_CODE == null) {
            errorMessages.add(mailMessageSource.getString(MAIL_VERIFICATION_EMAIL_NOT_SENT));
        } else if (verificationCode == null) {
            errorMessages.add(mailMessageSource.getString(MAIL_VERIFICATION_CODE_NULL));
        }


        if (EMAIL_VERIFICATION_CODE.equals(verificationCode)) {
            emailVerificationResult.setSuccess(true);
            emailVerificationResult.setResultMessage(successMessageSource.getString(EMAIL_VERIFICATION_SUCCESS));
        } else {
            errorMessages.add(mailMessageSource.getString(MAIL_VERIFICATION_EMAIL_FAILURE));
            emailVerificationResult.setErrorMessages(errorMessages);
        }

        return emailVerificationResult;
    }
}
