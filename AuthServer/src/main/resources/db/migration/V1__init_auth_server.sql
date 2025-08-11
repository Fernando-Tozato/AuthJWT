-- 1) Cria o schema auth (se ainda não existir)
CREATE SCHEMA IF NOT EXISTS auth;

-- 2) Garante que as próximas criações ocorram em auth
SET search_path TO auth;

-- 3) Tabelas do Spring Authorization Server
CREATE TABLE oauth2_registered_client (
                                          id VARCHAR(100) PRIMARY KEY,
                                          client_id VARCHAR(100) UNIQUE NOT NULL,
                                          client_secret VARCHAR(200),
                                          client_id_issued_at TIMESTAMP,
                                          client_secret_expires_at TIMESTAMP,
                                          client_name VARCHAR(200),
                                          client_authentication_methods VARCHAR(100),
                                          authorization_grant_types VARCHAR(100),
                                          redirect_uris VARCHAR(2000),
                                          scopes VARCHAR(2000),
                                          client_settings VARCHAR(5000),
                                          token_settings VARCHAR(5000)
);

CREATE TABLE oauth2_authorization (
                                      id VARCHAR(100) PRIMARY KEY,
                                      registered_client_id VARCHAR(100) NOT NULL,
                                      principal_name VARCHAR(200) NOT NULL,
                                      authorization_grant_type VARCHAR(100) NOT NULL,
                                      attributes VARCHAR(2000),
                                      state VARCHAR(500),
                                      authorization_code_value BYTEA,
                                      authorization_code_issued_at TIMESTAMP,
                                      authorization_code_expires_at TIMESTAMP,
                                      authorization_code_metadata VARCHAR(2000),
                                      access_token_value BYTEA,
                                      access_token_issued_at TIMESTAMP,
                                      access_token_expires_at TIMESTAMP,
                                      access_token_metadata VARCHAR(2000),
                                      access_token_type VARCHAR(100),
                                      access_token_scopes VARCHAR(2000),
                                      refresh_token_value BYTEA,
                                      refresh_token_issued_at TIMESTAMP,
                                      refresh_token_expires_at TIMESTAMP,
                                      refresh_token_metadata VARCHAR(2000),
                                      oidc_id_token_value BYTEA,
                                      oidc_id_token_issued_at TIMESTAMP,
                                      oidc_id_token_expires_at TIMESTAMP,
                                      oidc_id_token_metadata VARCHAR(2000),
                                      CONSTRAINT fk_oauth2_client FOREIGN KEY (registered_client_id)
                                          REFERENCES oauth2_registered_client(id) ON DELETE CASCADE
);

CREATE TABLE oauth2_authorization_consent (
                                              registered_client_id VARCHAR(100) NOT NULL,
                                              principal_name VARCHAR(200) NOT NULL,
                                              authorities VARCHAR(1000),
                                              PRIMARY KEY (registered_client_id, principal_name),
                                              CONSTRAINT fk_oauth2_consent_client FOREIGN KEY (registered_client_id)
                                                  REFERENCES oauth2_registered_client(id) ON DELETE CASCADE
);
