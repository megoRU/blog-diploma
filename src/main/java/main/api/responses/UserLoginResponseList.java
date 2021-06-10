package main.api.responses;

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
        this.user.setId(user.getId());
        this.user.setName(user.getName());
        this.user.setPhoto(user.getPhoto());
        this.user.setEmail(user.getEmail());
        this.user.setModeration(user.getIsModerator() == 1);
        this.user.setModerationCount(0);
        this.user.setSettings(true);

    }
}
