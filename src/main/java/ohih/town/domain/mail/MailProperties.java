package ohih.town.domain.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MailProperties {
    private String from = "ohih0216@gmail.com";
    private String to;
    private String subject = "This is mail title.";
    private String body;

    public MailProperties(String code) {
        this.body = "Verification code is " + code + ".";
    }
}
