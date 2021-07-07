package main.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {

    @JsonProperty("e_mail")
    private String email;
    private String password;
    private String name;
    private String captcha;
    private String captcha_secret;

    @Override
    public String toString() {
        return "RegistrationRequest{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", captcha='" + captcha + '\'' +
                ", captcha_secret='" + captcha_secret + '\'' +
                '}';
    }
}