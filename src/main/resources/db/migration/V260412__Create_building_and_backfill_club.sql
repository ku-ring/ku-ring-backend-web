-- building 마스터 테이블 생성
CREATE TABLE building
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    lat  DOUBLE,
    lon  DOUBLE,
    CONSTRAINT uk_building_name UNIQUE (name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- club이 building을 참조할 수 있도록 FK 컬럼 추가
ALTER TABLE club
    ADD COLUMN building_id BIGINT NULL;

ALTER TABLE club
    ADD CONSTRAINT fk_club_building
        FOREIGN KEY (building_id) REFERENCES building (id);
