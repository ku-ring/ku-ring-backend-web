ALTER TABLE building
    ADD COLUMN address VARCHAR(100) NULL AFTER name,
    ADD COLUMN image_path VARCHAR(255) NULL AFTER lon;

UPDATE building
SET address = '서울특별시 광진구 능동로 120'
WHERE address IS NULL;

ALTER TABLE building
    MODIFY COLUMN address VARCHAR(100) NOT NULL;

CREATE TABLE building_search_keyword
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    building_id BIGINT      NOT NULL,
    keyword     VARCHAR(50) NOT NULL,
    CONSTRAINT uk_building_search_keyword UNIQUE (building_id, keyword),
    CONSTRAINT fk_building_search_keyword_building
        FOREIGN KEY (building_id) REFERENCES building (id) ON DELETE CASCADE,
    INDEX idx_building_search_keyword_keyword (keyword)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE campus_place_category
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    code           VARCHAR(50) NOT NULL,
    kor_name       VARCHAR(50) NOT NULL,
    display_order  INT         NOT NULL,
    filter_enabled BOOLEAN     NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_campus_place_category_code UNIQUE (code),
    INDEX idx_campus_place_category_filter_order (filter_enabled, display_order)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE campus_place
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    building_id     BIGINT       NOT NULL,
    category_id     BIGINT       NOT NULL,
    name            VARCHAR(100) NOT NULL,
    image_path      VARCHAR(255) NULL,
    location_type   VARCHAR(20)  NOT NULL,
    floor           VARCHAR(20)  NULL,
    location_detail VARCHAR(255) NULL,
    quantity        INT          NULL,
    external_url    VARCHAR(500) NULL,
    display_order   INT          NOT NULL DEFAULT 0,
    CONSTRAINT fk_campus_place_building
        FOREIGN KEY (building_id) REFERENCES building (id) ON DELETE CASCADE,
    CONSTRAINT fk_campus_place_category
        FOREIGN KEY (category_id) REFERENCES campus_place_category (id),
    CONSTRAINT ck_campus_place_quantity CHECK (quantity IS NULL OR quantity > 0),
    INDEX idx_campus_place_building_order (building_id, display_order),
    INDEX idx_campus_place_category (category_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE building_operating_hours
(
    building_id BIGINT      NOT NULL,
    period      VARCHAR(20) NOT NULL,
    day_group   VARCHAR(20) NOT NULL,
    status      VARCHAR(30) NOT NULL,
    opens_at    TIME        NULL,
    closes_at   TIME        NULL,
    CONSTRAINT uk_building_operating_hours UNIQUE (building_id, period, day_group),
    CONSTRAINT fk_building_operating_hours_building
        FOREIGN KEY (building_id) REFERENCES building (id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE campus_place_operating_hours
(
    campus_place_id BIGINT      NOT NULL,
    period          VARCHAR(20) NOT NULL,
    day_group       VARCHAR(20) NOT NULL,
    status          VARCHAR(30) NOT NULL,
    opens_at        TIME        NULL,
    closes_at       TIME        NULL,
    CONSTRAINT uk_campus_place_operating_hours UNIQUE (campus_place_id, period, day_group),
    CONSTRAINT fk_campus_place_operating_hours_place
        FOREIGN KEY (campus_place_id) REFERENCES campus_place (id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
