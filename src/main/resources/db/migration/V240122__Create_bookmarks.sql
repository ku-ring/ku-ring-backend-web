create table user_bookmarks
(
    id        bigint       not null,
    notice_id varchar(255) not null,
    constraint FK_userTBL_bookmarksTBL foreign key (id) references user (id)
);
