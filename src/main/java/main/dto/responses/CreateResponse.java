package main.dto.responses;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Data
@Setter(AccessLevel.NONE)
@NoArgsConstructor
public class CreateResponse {

    private boolean result;
    private Map<String, String> errors;

    public CreateResponse(boolean result, Map<String, String> errors) {
        this.result = result;
        this.errors = errors;
    }
}
