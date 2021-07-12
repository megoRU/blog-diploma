package main.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import main.model.User;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {

    private boolean result;
    private UserLoginResponse user;

    public LoginResponse(Boolean result, User user, Integer moderationCount) {
        this.result = result;
        this.user = new UserLoginResponse(user, moderationCount);
    }
}
