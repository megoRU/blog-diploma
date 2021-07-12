package main.dto.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RegistrationErrors {

    @JsonProperty("email")
    EMAIL("Этот e-mail уже зарегистрирован"),
    @JsonProperty("name")
    NAME("Имя указано неверно"),
    @JsonProperty("password")
    PASSWORD("Пароль короче 6-ти символов"),
    @JsonProperty("captcha")
    CAPTCHA("Код с картинки введён неверно");

    private final String errors;

    RegistrationErrors(String errors) {
        this.errors = errors;
    }

    public String getErrors() {
        return errors;
    }
}
