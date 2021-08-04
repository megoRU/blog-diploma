package main.dto.enums;

public enum ProfileErrors {

    EMAIL("Этот e-mail уже зарегистрирован"),
    PHOTO_MAX_5MB("Фото слишком большое, нужно не более 5 Мб"),
    NAME("Имя указано неверно"),
    PASSWORD("Пароль короче 6-ти символов"),
    PHOTO_TO_HIGH_FOR_COMMENTS("Размер файла превышает допустимый размер"),
    IMAGE_BAD_FORMAT("Формат изображения не поддерживается"),
    PHOTO_NULL("Вы прислали пустой файл"),
    IS_NOT_PHOTO("Вы прислали не фотографию");

    private final String errors;

    ProfileErrors(String errors) {
        this.errors = errors;
    }

    public String getErrors() {
        return errors;
    }
}