-- 학사일정 카테고리 컬럼 사이즈를 20에서 30으로 확장
ALTER TABLE academic_event MODIFY COLUMN category VARCHAR (30);