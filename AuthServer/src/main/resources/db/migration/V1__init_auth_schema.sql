CREATE SCHEMA IF NOT EXISTS auth;

CREATE TABLE IF NOT EXISTS auth.users (
                                          id             UUID PRIMARY KEY,
                                          username       varchar UNIQUE NOT NULL,
                                          email          varchar UNIQUE NOT NULL,
                                          password_hash  VARCHAR(100) NOT NULL,
                                          enabled        BOOLEAN NOT NULL DEFAULT TRUE,
                                          locked         BOOLEAN NOT NULL DEFAULT FALSE,
                                          created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                          updated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS auth.roles (
                                          id    BIGSERIAL PRIMARY KEY,
                                          name  VARCHAR(64) UNIQUE NOT NULL,
                                          active BOOLEAN NOT NULL DEFAULT TRUE,
                                          created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                          updated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS auth.user_roles (
                                               id       BIGSERIAL PRIMARY KEY,
                                               user_id  UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
                                               role_id  BIGINT NOT NULL REFERENCES auth.roles(id) ON DELETE CASCADE,
                                               UNIQUE(user_id, role_id)
);

CREATE TABLE IF NOT EXISTS auth.refresh_tokens (
                                                   id           UUID PRIMARY KEY,
                                                   user_id      UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
                                                   token_hash   VARCHAR(200) NOT NULL,
                                                   expires_at   TIMESTAMPTZ NOT NULL,
                                                   revoked      BOOLEAN NOT NULL DEFAULT FALSE,
                                                   created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
