UPDATE notice
SET url = REPLACE(url, 'https://www.konkuk.ac.kr', 'https://old.konkuk.ac.kr')
WHERE notice.category_name != 'DEPARTMENT' AND notice.category_name != 'LIBRARY';
