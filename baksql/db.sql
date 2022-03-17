-- auto-generated definition
create table subscribe_user
(
    email            varchar(100) not null,
    server           varchar(20)  not null,
    alert_start_time datetime,
    qq               varchar(20)
);

create unique index subscribe_users_email_server_uindex
    on subscribe_user (email, server);

