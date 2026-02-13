CREATE TABLE club
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    name              VARCHAR(30) NOT NULL,
    summary           VARCHAR(30) NOT NULL,
    description       TEXT,
    category          VARCHAR(30) NOT NULL,
    division          VARCHAR(30) NOT NULL,
    poster_image_path VARCHAR(255),
    building          VARCHAR(30),
    room              VARCHAR(30),
    lat DOUBLE,
    lon DOUBLE,
    recruit_start_at  DATETIME,
    recruit_end_at    DATETIME,
    is_always         BOOLEAN     NOT NULL DEFAULT false,
    apply_url         VARCHAR(255),
    qualifications    TEXT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE club_subscribe
(
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    club_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,

    CONSTRAINT fk_club_subscribe_club
        FOREIGN KEY (club_id) REFERENCES club (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_club_subscribe_user
        FOREIGN KEY (user_id) REFERENCES user (id)
            ON DELETE CASCADE,

    CONSTRAINT uk_club_user UNIQUE (club_id, user_id),

    INDEX idx_club_subscribe_user (user_id),
    INDEX idx_club_subscribe_club (club_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE club_sns
(
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    club_id BIGINT       NOT NULL,
    type    VARCHAR(30)  NOT NULL,
    url     VARCHAR(255) NOT NULL,

    CONSTRAINT fk_club_sns_club
        FOREIGN KEY (club_id) REFERENCES club (id)
            ON DELETE CASCADE,

    INDEX   idx_club_sns_club (club_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;