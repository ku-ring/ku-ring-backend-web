DELETE
FROM notice
WHERE notice.category_name != 'LIBRARY' AND dtype = 'Notice';
