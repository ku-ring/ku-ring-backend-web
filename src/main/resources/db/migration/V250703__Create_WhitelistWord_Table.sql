CREATE TABLE IF NOT EXISTS `whitelist_word`
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    word        VARCHAR(128) NOT NULL,
    description VARCHAR(256),
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  DATETIME     NOT NULL,
    updated_at  DATETIME     NOT NULL,

    PRIMARY KEY (id)
)ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;
