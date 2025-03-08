-- 1. root_user 테이블 생성
CREATE TABLE root_user
(
    id             bigint              not null AUTO_INCREMENT,
    email          varchar(256) unique not null,
    password       varchar(256)        not null,
    nickname       varchar(256) unique not null,
    question_count int                 not null,
    primary key (id)
);

-- 2.  login_user_id 추가
ALTER TABLE user
    ADD COLUMN login_user_id bigint null;

ALTER TABLE user
    CHANGE COLUMN token fcm_token VARCHAR(256);

ALTER TABLE user
    ADD CONSTRAINT FK_userTBL_root_userTBL FOREIGN KEY (login_user_id) REFERENCES root_user (id)
        ON DELETE SET NULL;