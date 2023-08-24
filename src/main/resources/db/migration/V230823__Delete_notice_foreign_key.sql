set @var=if((SELECT true FROM information_schema.TABLE_CONSTRAINTS WHERE
            CONSTRAINT_SCHEMA = DATABASE() AND
            TABLE_NAME        = 'notice' AND
            CONSTRAINT_NAME   = 'FK2pjg4hwditxubnvu938yptsck' AND
            CONSTRAINT_TYPE   = 'FOREIGN KEY') = true,
    'alter table notice drop foreign key FK2pjg4hwditxubnvu938yptsck',
    'select 1');

prepare stmt from @var;
execute stmt;
deallocate prepare stmt;
