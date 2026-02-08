CREATE TABLE club
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    name              VARCHAR(30) NOT NULL,
    summary           VARCHAR(30) NOT NULL,
    description       TEXT,
    category          VARCHAR(30) NOT NULL,
    division          VARCHAR(30) NOT NULL,
    instagram_url     VARCHAR(255),
    poster_image_path VARCHAR(255),
    building          VARCHAR(30),
    room              VARCHAR(30),
    x DOUBLE,
    y DOUBLE,
    recruit_start_at  DATETIME,
    recruit_end_at    DATETIME,
    is_always         BOOLEAN     NOT NULL DEFAULT false,
    apply_url         VARCHAR(255),
    qualifications    TEXT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE club_bookmark
(
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    club_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,

    CONSTRAINT fk_club_bookmark_club
        FOREIGN KEY (club_id) REFERENCES club (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_club_bookmark_user
        FOREIGN KEY (user_id) REFERENCES user (id)
            ON DELETE CASCADE,

    CONSTRAINT uk_club_user UNIQUE (club_id, user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

