create table t_country_codes
(
    KEY_COUNTRY_CODE varchar(3) charset utf8mb3  not null
        primary key,
    str_country_name varchar(50) charset utf8mb3 null
);

create table t_paramters
(
    str_key         varchar(255) not null
        primary key,
    str_value       varchar(255) null,
    str_description varchar(255) null,
    b_status        tinyint(1)   null
)
    charset = utf8mb3;

create table t_role
(
    lg_role_id           varchar(20)                        not null
        primary key,
    str_role_description varchar(50)                        null,
    b_active             tinyint  default 0                 null,
    dt_created           datetime default CURRENT_TIMESTAMP not null
);

create table t_token
(
    lg_token_id varchar(50) charset utf8mb3  not null
        primary key,
    str_refresh varchar(255) charset utf8mb3 null,
    status      varchar(10) charset utf8mb3  null,
    dt_created  timestamp                    null
);

create table t_users
(
    user_id            varchar(50)                  not null
        primary key,
    name               varchar(255) charset utf8mb3 null,
    username           varchar(255) charset utf8mb3 not null,
    phone              varchar(15) charset utf8mb3  null,
    password           varchar(255) charset utf8mb3 not null,
    status             varchar(15) charset utf8mb3  null,
    dt_created         datetime                     null,
    dt_updated         datetime                     null,
    email              varchar(40) charset utf8mb3  null,
    str_login_provider varchar(15) default 'local'  null
);

create table t_role_user
(
    lg_role_user_id bigint auto_increment
        primary key,
    lg_role_id      varchar(20)                         null,
    user_id         varchar(50)                         null,
    dt_created      timestamp default CURRENT_TIMESTAMP null,
    constraint t_role_user_ibfk_1
        foreign key (lg_role_id) references t_role (lg_role_id),
    constraint t_role_user_t_users_user_id_fk
        foreign key (user_id) references t_users (user_id)
);

create index lg_role_id
    on t_role_user (lg_role_id);

create index `user status`
    on t_users (status);

