package main.api.responses;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
public class PostsResponse {

    private int count;
    private List<PostResponseForList> posts;
}
