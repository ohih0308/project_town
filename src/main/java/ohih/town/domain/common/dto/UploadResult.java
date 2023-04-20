package ohih.town.domain.common.dto;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadResult {
    private boolean success;
    private Map<String, String> errorMessages = new HashMap<>();
    private String resultMessage;
}
