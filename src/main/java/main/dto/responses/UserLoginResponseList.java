package main.dto.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.model.User;

@Setter
@Getter
@NoArgsConstructor
public class UserLoginResponseList {

    private boolean result;
    private UserLoginResponse user;

    public UserLoginResponseList(User user) {
        this.user = new UserLoginResponse(user);
    }
}
