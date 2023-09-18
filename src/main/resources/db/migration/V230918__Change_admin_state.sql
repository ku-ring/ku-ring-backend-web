TRUNCATE TABLE 'admin';

ALTER TABLE 'admin' DROP COLUMN 'token';
ALTER TABLE 'admin' DROP COLUMN 'owner';

ALTER TABLE 'admin' ADD 'admin_login_id' varchar(256) not null;
ALTER TABLE 'admin' ADD 'admin_password' varchar(256) not null;

create table 'admin_roles' (
    id bigint not null foreign key references 'admin'('id'),
    'role' varchar(256) not null
)
