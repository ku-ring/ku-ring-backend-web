INSERT INTO category (name)
SELECT 'bachelor' FROM DUAL WHERE NOT EXISTS(SELECT * FROM category WHERE name = 'bachelor');

INSERT INTO category (name)
SELECT 'scholarship' FROM DUAL WHERE NOT EXISTS(SELECT * FROM category WHERE name = 'scholarship');

INSERT INTO category (name)
SELECT 'employment' FROM DUAL WHERE NOT EXISTS(SELECT * FROM category WHERE name = 'employment');

INSERT INTO category (name)
SELECT 'national' FROM DUAL WHERE NOT EXISTS(SELECT * FROM category WHERE name = 'national');

INSERT INTO category (name)
SELECT 'student' FROM DUAL WHERE NOT EXISTS(SELECT * FROM category WHERE name = 'student');

INSERT INTO category (name)
SELECT 'industry_university' FROM DUAL WHERE NOT EXISTS(SELECT * FROM category WHERE name = 'industry_university');

INSERT INTO category (name)
SELECT 'normal' FROM DUAL WHERE NOT EXISTS(SELECT * FROM category WHERE name = 'normal');