package ohih.town.domain.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Register {
    private Long userId;
    private String email;
    private String username;
    private String password;

    public Register(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }
}
