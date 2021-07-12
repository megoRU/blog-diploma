package main.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class PostsResponse {

    private int count;
    private List<PostResponseForList> posts;
}
