CREATE TABLE IF NOT EXISTS `report`
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    target_id   BIGINT       NOT NULL,
    target_type VARCHAR(30)  NOT NULL,
    content     VARCHAR(256) NOT NULL,
    reporter_id BIGINT,
    created_at  DATETIME     NOT NULL,
    updated_at  DATETIME     NOT NULL,

    PRIMARY KEY(id),

    CONSTRAINT FK_reportTBL_userTBL
    FOREIGN KEY(reporter_id) REFERENCES user(id) ON DELETE SET NULL
) ENGINE = InnoDB
DEFAULT CHARSET = utf8mb4
COLLATE = utf8mb4_unicode_ci;