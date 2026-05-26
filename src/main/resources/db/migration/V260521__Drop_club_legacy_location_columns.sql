-- club 상세 위치 조회가 building_id 참조로 전환되고 backfill이 완료된 후 legacy 위치 컬럼을 제거한다.
ALTER TABLE club
    DROP COLUMN IF EXISTS building,
    DROP COLUMN IF EXISTS lat,
    DROP COLUMN IF EXISTS lon;
