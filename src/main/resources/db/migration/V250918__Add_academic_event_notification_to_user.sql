-- 사용자 테이블에 학사일정 알림 설정 컬럼 추가
ALTER TABLE user
    ADD COLUMN academic_event_notification_enabled BOOLEAN NOT NULL DEFAULT TRUE;

-- 기존 데이터에 대해서도 명시적으로 true 설정
UPDATE user
SET academic_event_notification_enabled = TRUE
WHERE academic_event_notification_enabled IS NULL;