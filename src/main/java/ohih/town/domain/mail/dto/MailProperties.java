package ohih.town.domain.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ohih.town.constants.ConfigurationConst;
import ohih.town.constants.ConfigurationResourceBundle;

import java.util.ResourceBundle;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MailProperties {
//    private String from = ConfigurationResourceBundle.MAIL.getString(ConfigurationConst.MAIL_VERIFICATION_FROM);
    private String to;
    private String subject = "This is mail title.";
    private String body;

    public MailProperties(String code) {
        this.body = "Verification code is " + code + ".";
    }
}
