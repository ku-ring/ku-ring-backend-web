CREATE TABLE IF NOT EXISTS `alert`
(
    alert_time datetime(6)  not null,
    created_at datetime(6),
    id         bigint       not null auto_increment,
    updated_at datetime(6),
    title      varchar(128) not null,
    content    varchar(256),
    status     enum ('PENDING','CANCELED','COMPLETED'),
    primary key (id)
) engine = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_unicode_ci;
