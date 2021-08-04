package main.dto.responses;

import lombok.Getter;
import lombok.Setter;
import main.model.User;

@Getter
@Setter
public class UserPostResponse {

    private int id;
    private String name;
    private String photo;

    public UserPostResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
    }
}
