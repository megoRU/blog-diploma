package main.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {

    private boolean result;
    @JsonProperty("user")
    private UserLoginResponse userLoginResponse;
}
