CREATE TABLE academic_event
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_uid      VARCHAR(255) NOT NULL,
    summary        VARCHAR(255) NOT NULL,
    description    TEXT,
    category       VARCHAR(20),
    transparent    VARCHAR(20)  NOT NULL,
    sequence       INT          NOT NULL,
    start_time     DATETIME     NOT NULL,
    end_time       DATETIME     NOT NULL,
    notify_enabled BOOLEAN      NOT NULL,
    created_at     DATETIME(6),
    updated_at     DATETIME(6)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;