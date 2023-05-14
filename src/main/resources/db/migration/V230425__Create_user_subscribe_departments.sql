create table user_departments (
    id bigint not null,
    department_name varchar(255) not null,
    constraint FK_userTBL_departmentsTBL foreign key (id) references user (id)
);
