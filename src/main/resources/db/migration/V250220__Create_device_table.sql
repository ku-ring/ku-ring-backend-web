-- 1. fcm_token 테이블 생성
CREATE TABLE device
(
    id             bigint              not null AUTO_INCREMENT,
    fcm_token      varchar(256) unique not null,
    user_id        bigint              not null,
    origin_user_id bigint              not null,
    primary key (id),
    constraint FK_userTBL_deviceTBL foreign key (user_id) references user (id)
);

-- 2. email, password 추가
ALTER TABLE user
    ADD COLUMN email VARCHAR(256) UNIQUE,
ADD COLUMN nickname VARCHAR(256) UNIQUE,
ADD COLUMN password VARCHAR(256);


-- 3. 기존 user 테이블의 fcmToken 값 fcm_token 테이블로 이동
INSERT INTO device (fcm_token, user_id, origin_user_id)
SELECT token, id, id
FROM user
WHERE token IS NOT NULL;


-- 4. user 테이블에서 fcmToken 컬럼 삭제
ALTER TABLE user DROP COLUMN token;
