# notice에서 2022.07.26 의 형태를 2022-07-26 으로 변경한다
UPDATE notice
SET posted_dt = REPLACE(posted_dt, '.', '-')
WHERE LENGTH(notice.posted_dt) = 10;

UPDATE notice
SET updated_dt = REPLACE(updated_dt, '.', '-')
WHERE LENGTH(notice.updated_dt) = 10;


# notice에서 2022-07-26 의 형태에 시분초를 추가해준다
UPDATE notice
SET notice.posted_dt = CONCAT(notice.posted_dt, ' 00:00:01')
WHERE LENGTH(notice.posted_dt) = 10;

UPDATE notice
SET notice.updated_dt = CONCAT(notice.updated_dt, ' 00:00:01')
WHERE LENGTH(notice.updated_dt) = 10;


# notice 테이블의 posted_dt, updated_dt 컬럼의 타입을 DATETIME으로 변경한다
ALTER TABLE notice MODIFY posted_dt DATETIME;
ALTER TABLE notice MODIFY updated_dt DATETIME;
