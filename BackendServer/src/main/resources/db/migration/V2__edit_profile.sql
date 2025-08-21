alter table app.profiles
    add column if not exists phone_number varchar(15);

