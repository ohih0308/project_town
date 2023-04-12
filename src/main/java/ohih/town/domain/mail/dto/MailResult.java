package ohih.town.domain.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MailResult {
    private boolean success;
    private String from;
    private String to;
    private String resultMessage;
    private Map<String, String> errorMessages;

    public MailResult(String from, String to) {
        this.from = from;
        this.to = to;
    }
}
