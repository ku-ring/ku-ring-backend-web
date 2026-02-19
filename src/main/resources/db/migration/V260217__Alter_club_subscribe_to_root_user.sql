-- 1) root_user FK로 전환하기 위한 임시 컬럼 추가
ALTER TABLE club_subscribe
    ADD COLUMN root_user_id BIGINT NULL;

-- 2) 기존 user_id -> user.login_user_id(root_user.id)로 데이터 이관
UPDATE club_subscribe cs
    JOIN user u ON cs.user_id = u.id
SET cs.root_user_id = u.login_user_id;

-- 3) 기존 user FK/unique 제약 제거
ALTER TABLE club_subscribe
    DROP FOREIGN KEY fk_club_subscribe_user;

ALTER TABLE club_subscribe
    DROP INDEX uk_club_user;

-- 4) 기존 user_id 컬럼 제거 및 root_user_id NOT NULL 강제
ALTER TABLE club_subscribe
    DROP COLUMN user_id;

ALTER TABLE club_subscribe
    MODIFY COLUMN root_user_id BIGINT NOT NULL;

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
