DELETE
FROM notice
WHERE notice.article_id IN (SELECT *
                            FROM (SELECT article_id
                                  FROM notice
                                  GROUP BY article_id
                                  HAVING COUNT(*) > 2) temp)
