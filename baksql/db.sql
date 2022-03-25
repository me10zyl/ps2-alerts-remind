-- auto-generated definition
create table subscribe_user
(
    email            varchar(100),
    server           varchar(20) not null,
    alert_start_time datetime,
    qq               varchar(20),
    is_qq_group      integer default 0
);

