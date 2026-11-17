UPDATE academic_event
SET end_time = DATE_SUB(end_time, INTERVAL 1 SECOND)
WHERE TIME(start_time) = '00:00:00'
  AND TIME(end_time) = '00:00:00'
  AND end_time > start_time;