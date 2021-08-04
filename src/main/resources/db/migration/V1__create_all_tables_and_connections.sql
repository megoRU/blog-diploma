drop table if exists captcha_codes;
drop table if exists global_settings;
drop table if exists post_comments;
drop table if exists post_votes;
drop table if exists posts;
drop table if exists tag2post;
drop table if exists tags;
drop table if exists users;

create table captcha_codes
(
    id          integer      not null auto_increment,
    code        varchar(255) not null,
    secret_code varchar(255) not null,
    time        datetime(6) not null,
    primary key (id)
) ENGINE=InnoDB;

create table global_settings
(
    id    integer      not null auto_increment,
    code  varchar(255) not null,
    name  varchar(255) not null,
    value varchar(255) not null,
    primary key (id)
) ENGINE=InnoDB;

create table post_comments
(
    id        integer not null auto_increment,
    parent_id integer,
    text      TEXT    not null,
    time      datetime(6) not null,
    post_id   integer not null,
    user_id   integer not null,
    primary key (id)
) ENGINE=InnoDB;

create table post_votes
(
    id      integer not null auto_increment,
    time    datetime(6) not null,
    value   integer not null,
    post_id integer not null,
    user_id integer not null,
    primary key (id)
) ENGINE=InnoDB;

create table posts
(
    id                integer      not null auto_increment,
    is_active         integer      not null,
    moderation_status enum('NEW', 'ACCEPTED', 'DECLINED') not null,
    moderator_id      integer,
    text              TEXT         not null,
    time              DATETIME     not null,
    title             varchar(255) not null,
    view_count        integer      not null,
    user_id           integer      not null,
    primary key (id)
) ENGINE=InnoDB;

create table tag2post
(
    id      integer not null auto_increment,
    post_id integer not null,
    tag_id  integer not null,
    primary key (id)
) ENGINE=InnoDB;


create table tags
(
    id   integer      not null auto_increment,
    name varchar(255) not null,
    primary key (id)

) ENGINE=InnoDB;

create table users
(
    id           integer      not null auto_increment,
    code         varchar(255),
    email        varchar(255) not null,
    is_moderator integer      not null,
    name         varchar(255) not null,
    password     varchar(255) not null,
    photo        TEXT,
    reg_time     datetime(6) not null,
    hash_time    datetime(6),
    primary key (id)
) ENGINE=InnoDB;

alter table post_comments add constraint POST_COMMENTS_POST_ID foreign key (post_id) references posts (id);
alter table post_comments add constraint POST_COMMENTS_USER_ID foreign key (user_id) references users (id);
alter table post_votes add constraint POST_VOTES_POST_ID foreign key (post_id) references posts (id);
alter table post_votes add constraint POST_VOTES_USER_ID foreign key (user_id) references users (id);
alter table posts add constraint POSTS_USER_ID foreign key (user_id) references users (id);
alter table tag2post add constraint TAG2POST_POSTS_ID foreign key (post_id) references posts (id);
alter table tag2post add constraint TAG2POST_TAGS_ID foreign key (tag_id) references tags (id);