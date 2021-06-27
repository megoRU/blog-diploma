package main.dto.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.model.PostComment;
import java.time.ZoneOffset;

@Setter
@Getter
@NoArgsConstructor
public class PostCommentsResponse {

    private long id;
    private long timestamp;
    private String text;
    private UserPostResponse user;

    public PostCommentsResponse(PostComment postComment) {
        this.id = postComment.getId();
        this.timestamp = postComment.getTime().toEpochSecond(ZoneOffset.ofHours(3));
        this.text = postComment.getText();
        this.user = new UserPostResponse(postComment.getUser());
    }
}
