package main.dto.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.dto.enums.PostErrors;

import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
public class CreatePostResponse {

    private boolean result;
    private Map<PostErrors, String> errors;

    public CreatePostResponse(boolean result, Map<PostErrors, String> errors) {
        this.result = result;
        this.errors = errors;
    }
}
