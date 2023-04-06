package ohih.town.domain.mail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.domain.mail.EmailVerificationResult;
import ohih.town.domain.mail.MailProperties;
import ohih.town.domain.mail.EmailVerificationRequest;
import ohih.town.utilities.Utilities;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static ohih.town.constants.ErrorMessageResourceBundle.MAIL_ERROR_MESSAGES;
import static ohih.town.constants.ErrorsConst.EMAIL_VERIFICATION_FAILURE;
import static ohih.town.constants.SuccessConst.EMAIL_VERIFICATION_SUCCESS;
import static ohih.town.constants.SuccessMessagesResourceBundle.SUCCESS_MESSAGES;
import static ohih.town.constants.UserConst.VERIFICATION_CODE_LENGTH;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final JavaMailSender javaMailSender;


    public EmailVerificationRequest sendVerificationCode(String email) throws MailException {
        EmailVerificationRequest emailVerificationRequest = new EmailVerificationRequest();

        String verificationCode = Utilities.createCode(VERIFICATION_CODE_LENGTH);
        emailVerificationRequest.setVerificationCode(verificationCode);
        emailVerificationRequest.setEmail(email);

        MailProperties mailProperties = new MailProperties(verificationCode);
        mailProperties.setTo(email);

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(mailProperties.getFrom());
        simpleMailMessage.setTo(mailProperties.getTo());
        simpleMailMessage.setSubject(mailProperties.getSubject());
        simpleMailMessage.setText(mailProperties.getBody());

        try {
            javaMailSender.send(simpleMailMessage);
        } catch (MailException e) {
            throw e;
        }

        log.info("verificationCode = {}", verificationCode);
        return emailVerificationRequest;
    }

    public EmailVerificationResult verifyEmailCode(EmailVerificationRequest EMAIL_VERIFICATION_REQUEST, String emailVerificationCode) {
        EmailVerificationResult emailVerificationResult = new EmailVerificationResult();

        if (emailVerificationCode == null || !emailVerificationCode.equals(EMAIL_VERIFICATION_REQUEST.getVerificationCode())) {
            emailVerificationResult.setVerifiedEmail(null);
            emailVerificationResult.setSuccess(false);
            emailVerificationResult.setMessage(MAIL_ERROR_MESSAGES.getString(EMAIL_VERIFICATION_FAILURE));
        } else {
            emailVerificationResult.setVerifiedEmail(EMAIL_VERIFICATION_REQUEST.getEmail());
            emailVerificationResult.setSuccess(true);
            emailVerificationResult.setMessage(SUCCESS_MESSAGES.getString(EMAIL_VERIFICATION_SUCCESS));
        }

        return emailVerificationResult;
    }
}
