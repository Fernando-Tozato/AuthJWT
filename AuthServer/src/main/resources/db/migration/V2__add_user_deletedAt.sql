alter table auth.users add column if not exists deleted_at timestamptz null;
