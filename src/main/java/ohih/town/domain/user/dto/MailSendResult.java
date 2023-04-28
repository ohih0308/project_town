package ohih.town.domain.mail.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailSendResult {
    private boolean isSent;
    private String from;
    private String to;
    private String resultMessage;
    private Map<String, String> errorMessages;
}