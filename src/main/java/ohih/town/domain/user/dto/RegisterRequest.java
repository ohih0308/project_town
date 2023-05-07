package ohih.town.domain.user.dto;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RegisterRequest {

    private Long userId;

    private String email;
    private String username;
    private String password;
    private String passwordConfirmation;
    private boolean agreement = false;
}
