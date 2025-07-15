INSERT INTO bad_word (word, category, description, is_active, created_at, updated_at)
VALUES ('병신', 'PROFANITY', '욕설', true, NOW(), NOW()),
       ('ㅂㅅ', 'PROFANITY', '욕설', true, NOW(), NOW()),
       ('시발', 'PROFANITY', '욕설', true, NOW(), NOW());

INSERT INTO whitelist_word (word, description, is_active, created_at, updated_at)
VALUES ('시발점', '욕설', true, NOW(), NOW()),
       ('시발자동차', '욕설', true, NOW(), NOW());