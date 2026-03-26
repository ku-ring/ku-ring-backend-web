SET @index_exists := (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'club_subscribe'
      AND index_name = 'idx_club_subscribe_club'
);

SET @sql := IF(
    @index_exists > 0,
    'ALTER TABLE club_subscribe DROP INDEX idx_club_subscribe_club',
    'SELECT 1'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
