create schema if not exists app;

create table if not exists app.profiles (
                                            id uuid primary key,
                                            user_id uuid not null unique,
                                            first_name varchar(100),
                                            last_name  varchar(100),
                                            bio text,
                                            avatar_url text,
                                            created_at timestamptz not null default now(),
                                            updated_at timestamptz not null default now()
);

create index if not exists idx_profiles_user_id on app.profiles(user_id);