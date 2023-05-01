SET foreign_key_checks = 0
UPDATE category SET name = UPPER(name);
UPDATE user_category SET category_name = UPPER(category_name);
UPDATE notice SET category_name = UPPER(category_name);
SET foreign_key_checks = 1
