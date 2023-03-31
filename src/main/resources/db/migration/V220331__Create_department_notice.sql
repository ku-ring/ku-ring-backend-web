alter table notice
    add column dtype varchar(31) not null;

alter table notice
    add column department_name varchar(31);

alter table notice
    add column url varchar(256);

alter table notice
    add column important boolean;

alter TABLE feedback CHANGE uid user_id bigint not null;

update notice SET dtype = 'Notice';

update notice SET notice.important = false;

update notice SET url = CASE
WHEN notice.category_name = 'library' THEN CONCAT('https://library.konkuk.ac.kr/library-guide/bulletins/notice/', notice.article_id)
ELSE CONCAT('https://www.konkuk.ac.kr/do/MessageBoard/ArticleRead.do?id=', notice.article_id) END;

insert into category (name) values('department');
