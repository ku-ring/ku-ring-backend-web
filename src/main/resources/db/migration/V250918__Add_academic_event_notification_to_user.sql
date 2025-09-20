-- 사용자 테이블에 학사일정 알림 설정 컬럼 추가
ALTER TABLE user
    ADD COLUMN academic_event_notification_enabled BOOLEAN NOT NULL DEFAULT TRUE;