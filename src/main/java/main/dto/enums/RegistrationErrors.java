package main.dto.enums;

public enum RegistrationErrors {

    EMAIL("Этот e-mail уже зарегистрирован"),
    NAME("Имя указано неверно"),
    PASSWORD("Пароль короче 6-ти символов"),
    CAPTCHA("Код с картинки введён неверно"),
    CODE("Ссылка для восстановления пароля устарела.<br><br><div><a href=\"/auth/restore\">Запросить ссылку снова</a>");

    private final String errors;

    RegistrationErrors(String errors) {
        this.errors = errors;
    }

    public String getErrors() {
        return errors;
    }
}