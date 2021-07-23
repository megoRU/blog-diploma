package main.dto.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
public class CreateResponse<T> {

    private boolean result;
    private Map<Class<T>, String> errors;

    public CreateResponse(boolean result, Map<Class<T>, String> errors) {
        this.result = result;
        this.errors = errors;
    }
}
