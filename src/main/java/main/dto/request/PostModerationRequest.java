package main.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PostModerationRequest {

    @JsonProperty("post_id")
    private Integer postId;
    private String decision;
}

