package ohih.town.domain.post.dto;

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
public class UploadResult {

    private boolean success;
    private Long postId;

    private String successMessage;

    private List<Map<String, Boolean>> fieldValidations;
    private List<Map<String, String>> messages;

    public UploadResult() {
        this.success = false;
    }
}
