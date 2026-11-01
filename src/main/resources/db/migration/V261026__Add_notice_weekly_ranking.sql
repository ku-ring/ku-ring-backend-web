ALTER TABLE notice
    ADD COLUMN view_count INT NOT NULL DEFAULT 0;

CREATE TABLE notice_view_snapshot
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(32) NOT NULL,
    article_id    VARCHAR(32) NOT NULL,
    view_count    INT         NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE notice_weekly_ranking
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    week_start_date DATE         NOT NULL,
    ranking         INT          NOT NULL,
    notice_id       BIGINT       NOT NULL,
    article_id      VARCHAR(32)  NOT NULL,
    subject         VARCHAR(128) NOT NULL,
    category_name   VARCHAR(32)  NOT NULL,
    posted_dt       DATETIME     NOT NULL,
    url             VARCHAR(256) NOT NULL,
    view_count      INT          NOT NULL,
    created_at      DATETIME(6),
    updated_at      DATETIME(6),

    INDEX           idx_notice_weekly_ranking_week_start_date (week_start_date)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
