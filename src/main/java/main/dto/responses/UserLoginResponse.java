package main.dto.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.model.User;

@Getter
@Setter
@NoArgsConstructor
public class UserLoginResponse {

    private long id;
    private String name;
    private String photo;
    private String email;
    private boolean moderation;
    private int moderationCount;
    private boolean settings;

    public UserLoginResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.photo = user.getPhoto();
        this.email = user.getEmail();
        this.moderation = user.getIsModerator() == 1;
        //Нужно прописать логику или выборку
        this.moderationCount = 0;
        //тут тоже
        this.settings = true;
    }
}