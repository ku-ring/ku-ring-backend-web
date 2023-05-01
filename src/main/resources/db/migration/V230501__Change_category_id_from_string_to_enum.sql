SET SESSION foreign_key_checks = 0;
UPDATE category SET category.name = UPPER(category.name);
UPDATE user_category SET category_name = UPPER(category_name);
UPDATE notice SET category_name = UPPER(category_name);
SET SESSION foreign_key_checks = 1;
