package main.dto.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.dto.enums.RegistrationErrors;

import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
public class RegistrationResponse {

    private boolean result;
    private Map<RegistrationErrors, String> errors;

    public RegistrationResponse(boolean result, Map<RegistrationErrors, String> errors) {
        this.result = result;
        this.errors = errors;
    }

    public RegistrationResponse(boolean result) {
        this.result = result;
    }
}
