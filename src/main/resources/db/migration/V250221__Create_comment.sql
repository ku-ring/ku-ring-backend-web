CREATE TABLE IF NOT EXISTS `comment`
(
    id         bigint not null auto_increment,
    parent_id    bigint null,
    user_id    bigint,
    notice_id  bigint,
    content    varchar(256),
    created_at datetime(6),
    updated_at datetime(6),
    destroyed_at datetime(6),
    primary key (id)
) engine = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_unicode_ci;
