package ohih.town.domain.mail.service;

import ohih.town.domain.mail.EmailVerificationRequest;
import ohih.town.domain.mail.EmailVerificationResult;
import ohih.town.domain.mail.MailProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static ohih.town.constants.SuccessConst.EMAIL_VERIFICATION_SUCCESS;
import static ohih.town.constants.SuccessMessagesResourceBundle.SUCCESS_MESSAGES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@SpringBootTest
class MailServiceTest {

    @Mock
    JavaMailSenderImpl javaMailSender;

    @InjectMocks
    @Autowired
    MailService mailService;


    @Test
    void sendVerificationCode() {
        MailProperties mailProperties = new MailProperties("0000");
        mailProperties.setTo("test@test.com");

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(mailProperties.getFrom());
        simpleMailMessage.setTo(mailProperties.getTo());
        simpleMailMessage.setSubject(mailProperties.getSubject());
        simpleMailMessage.setText(mailProperties.getBody());

        javaMailSender.send(simpleMailMessage);
//        javaMailSender.send(simpleMailMessage);

        verify(javaMailSender, times(1)).send(simpleMailMessage);

        assertEquals(1, Mockito.mockingDetails(javaMailSender).getInvocations().size());
    }

    @Test
    void verifyEmailCode() {
        // given
        EmailVerificationRequest request = new EmailVerificationRequest();
        request.setEmail("test@example.com");
        request.setVerificationCode("1234");

        // when
        EmailVerificationResult result = mailService.verifyEmailCode(request, "1234");

        // then
        Assertions.assertTrue(result.getSuccess());
        assertEquals("test@example.com", result.getVerifiedEmail());
        assertEquals(SUCCESS_MESSAGES.getString(EMAIL_VERIFICATION_SUCCESS), result.getMessage());
    }
}