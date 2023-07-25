SET SESSION foreign_key_checks = 0;

-- create user categories
create table user_categories
(
    id            bigint not null,
    category_name varchar(255),
    constraint fk_user_categories
        foreign key (id)
            references user (id)
);

-- insert user categories
insert into user_categories (id, category_name)
select u.id as id, uc.category_name as category_name
from user u join user_category uc
on u.token = uc.user_token;

DROP TABLE if exists user_category;
DROP TABLE if exists category;

SET SESSION foreign_key_checks = 1;
