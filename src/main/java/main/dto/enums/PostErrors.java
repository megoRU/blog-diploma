package main.dto.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PostErrors {

    @JsonProperty("title")
    TITLE("Заголовок не установлен"),
    @JsonProperty("text")
    TEXT("Текст публикации слишком короткий"),
    @JsonProperty("text")
    TEXT_COMMENT("Текст комментария не задан или слишком короткий");

    private final String errors;

    PostErrors(String errors) {
        this.errors = errors;
    }

    public String getErrors() {
        return errors;
    }
}