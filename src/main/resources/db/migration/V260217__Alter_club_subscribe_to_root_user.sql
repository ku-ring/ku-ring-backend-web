-- root_user로 바꾸면 데이터 이관을 해야하나, 아직 데이터가 존재하지 않는 개발 단계이므로 데이터 삭제하고 진행.
-- 1) 기존 device(user) 기준 구독 데이터는 유지하지 않는다.
DELETE FROM club_subscribe;

-- 2) 기존 user FK/unique/index 제약 제거
ALTER TABLE club_subscribe
    DROP FOREIGN KEY fk_club_subscribe_user;

ALTER TABLE club_subscribe
    DROP INDEX uk_club_user;

ALTER TABLE club_subscribe
    DROP INDEX idx_club_subscribe_user;

-- 3) root_user_id 컬럼 추가
ALTER TABLE club_subscribe
    ADD COLUMN root_user_id BIGINT NOT NULL;

-- 4) 기존 user_id 컬럼 제거
ALTER TABLE club_subscribe
    DROP COLUMN user_id;

-- 5) root_user FK 추가 (동아리/계정 삭제 시 구독도 함께 삭제)
ALTER TABLE club_subscribe
    ADD CONSTRAINT fk_club_subscribe_root_user
        FOREIGN KEY (root_user_id) REFERENCES root_user (id)
            ON DELETE CASCADE;

-- 6) 동아리-계정 구독 중복 방지 unique 제약 추가
ALTER TABLE club_subscribe
    ADD CONSTRAINT uk_club_root_user UNIQUE (club_id, root_user_id);

-- 7) root_user 기준 조회 성능을 위한 인덱스 추가
CREATE INDEX idx_club_subscribe_root_user ON club_subscribe (root_user_id);