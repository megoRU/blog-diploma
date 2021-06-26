INSERT INTO users (id,
                   code,
                   email,
                   is_moderator,
                   name, password,
                   photo, reg_time)
VALUES (1,
        "100000",
        "user@gmail.com",
        0,
        "Bob",
        "$2y$12$3pmWJ9YIggei5CfX/LiCy.h8otB0kkLYe9h0JPsUwAPm70MvIrcw.",
        null,
        now());

INSERT INTO users (id,
                   code,
                   email,
                   is_moderator,
                   name, password,
                   photo, reg_time)
VALUES (2,
        "200000",
        "moder@gmail.com",
        1,
        "moder",
        "$2y$12$3pmWJ9YIggei5CfX/LiCy.h8otB0kkLYe9h0JPsUwAPm70MvIrcw.",
        null,
        now());

INSERT INTO users (id,
                   code,
                   email,
                   is_moderator,
                   name, password,
                   photo, reg_time)
VALUES (3,
        "300000",
        "user2@gmail.com",
        1,
        "mego",
        "$2y$12$3pmWJ9YIggei5CfX/LiCy.h8otB0kkLYe9h0JPsUwAPm70MvIrcw.",
        null,
        now());