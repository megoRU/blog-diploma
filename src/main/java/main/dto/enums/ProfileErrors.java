package main.dto.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ProfileErrors {

    @JsonProperty("email")
    EMAIL("Этот e-mail уже зарегистрирован"),
    @JsonProperty("photo")
    PHOTO("Фото слишком большое, нужно не более 5 Мб"),
    @JsonProperty("name")
    NAME("Имя указано неверно"),
    @JsonProperty("password")
    PASSWORD("Пароль короче 6-ти символов"),
    @JsonProperty("image")
    IMAGE("Размер файла превышает допустимый размер"),
    @JsonProperty("image")
    IMAGE_BAD_FORMAT("Формат изображения не поддерживается"),
    @JsonProperty("image")
    IMAGE_NULL("Вы прислали пустой файл");

    private final String errors;

    ProfileErrors(String errors) {
        this.errors = errors;
    }

    public String getErrors() {
        return errors;
    }
}