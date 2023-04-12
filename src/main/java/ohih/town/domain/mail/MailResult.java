package ohih.town.domain.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MailResult {
    private String from;
    private String to;
    private Boolean isSent;
    private List<String> errorMessages;
}
