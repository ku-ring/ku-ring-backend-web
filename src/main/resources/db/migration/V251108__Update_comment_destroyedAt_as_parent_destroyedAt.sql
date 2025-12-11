-- 부모 댓글이 삭제되었으나(destroyed_at IS NOT NULL), 아직 Soft Delete 처리되지 않은 답글들을 업데이트합니다.

UPDATE comment AS child
    INNER JOIN comment AS parent
ON child.parent_id = parent.id
    SET child.destroyed_at = parent.destroyed_at -- 답글의 Soft Delete 시간을 부모의 삭제 시간과 동일하게 설정

WHERE
    child.parent_id IS NOT NULL  -- 1. 이 답글이 루트 댓글이 아님 (parent_id가 NULL이 아님)
  AND child.destroyed_at IS NULL -- 2. 답글이 아직 삭제되지 않았고 (destroyed_at IS NULL)
  AND parent.destroyed_at IS NOT NULL -- 3. 부모 댓글은 이미 삭제된 상태인 경우 (destroyed_at IS NOT NULL)
