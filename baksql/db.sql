-- auto-generated definition
create table subscribe_user
(
    email            varchar(100) not null,
    server           varchar(20)  not null,
    alert_start_time datetime,
    qq               varchar(20),
    is_send_mail     integer default 1,
    is_qq_group      integer default 0
);

create unique index subscribe_users_email_server_uindex
    on subscribe_user (email, server);

