package ohih.town;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.URLConst;
import ohih.town.constants.ViewConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TownController {

    @Autowired
    ApplicationContext applicationContext;


    @Value("#{verificationMail['mail.from']}")
    private String from;
    @Value("#{verificationMail['mail.verification.subject']}")
    private String subject;
    @Value("#{verificationMail['mail.verification.body']}")
    private String body;

    @GetMapping("/beans")
    public String beans() {
        String[] beans = applicationContext.getBeanDefinitionNames();

        for (String bean : beans) {
            log.info("bean name = {}", bean);
        }

        System.out.println("from = " + from);
        System.out.println("subject = " + subject);
        System.out.println("body = " + body.replace("${verification-code}", "hello"));
        return ViewConst.HOME;
    }


    @GetMapping(URLConst.HOME)
    public String home() {
        return ViewConst.HOME;
    }


    @GetMapping(URLConst.REGISTER)
    public String getRegisterForm() {
        return ViewConst.REGISTER;
    }



}
