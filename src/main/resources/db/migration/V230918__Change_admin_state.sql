drop table if exists `admin`;
drop table if exists `admin_roles`;

create table admin (
   id integer not null auto_increment,
   admin_login_id varchar(255) not null,
   admin_password varchar(255) not null,
   primary key (id)
);

create table admin_roles (
    id integer not null,
    role varchar(256) not null
);

alter table admin_roles
add constraint adr_fk_name foreign key (id) references admin (id);
