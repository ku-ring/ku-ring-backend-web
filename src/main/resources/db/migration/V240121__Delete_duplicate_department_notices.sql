delete
from notice
where notice.article_id in (select notice.article_id
                            from notice
                            group by notice.article_id
                            having COUNT(*) > 2);
