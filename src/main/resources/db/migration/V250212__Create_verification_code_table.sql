CREATE TABLE verification_code
(
    id    bigint       not null auto_increment,
    email varchar(128) not null,
    code  varchar(6)   not null,
    created_at datetime,
    updated_at datetime,
    primary key (id)
);