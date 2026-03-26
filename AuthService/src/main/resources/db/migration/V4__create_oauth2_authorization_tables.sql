-- 1. OIDC Client Table (With BaseModel UUID)
CREATE TABLE `client`
(
    id                            BINARY(16)    NOT NULL,
    created_at                    DATETIME      NOT NULL,
    updated_at                    DATETIME      NOT NULL,
    is_deleted                    BIT(1)        DEFAULT b'0' NOT NULL,
    client_id                     VARCHAR(255)  NOT NULL,
    client_id_issued_at           DATETIME      NOT NULL,
    client_secret                 TEXT          NOT NULL,
    client_secret_expires_at      DATETIME      NULL,
    client_name                   VARCHAR(255)  NOT NULL,
    client_authentication_methods VARCHAR(1000) NOT NULL,
    authorization_grant_types     VARCHAR(1000) NOT NULL,
    redirect_uris                 VARCHAR(1000) NULL,
    post_logout_redirect_uris     VARCHAR(1000) NULL,
    scopes                        VARCHAR(1000) NOT NULL,
    client_settings               TEXT          NOT NULL,
    token_settings                TEXT          NOT NULL,
    CONSTRAINT pk_client PRIMARY KEY (id),
    CONSTRAINT uc_client_client_id UNIQUE (client_id)
);

-- 2.  OAuth2 Authorization Table (String ID for Spring compatibility)
CREATE TABLE `authorization`
(
    id                            VARCHAR(255)  NOT NULL,
    registered_client_id          VARCHAR(255)  NOT NULL,
    principal_name                VARCHAR(255)  NOT NULL,
    authorization_grant_type      VARCHAR(255)  NOT NULL,
    created_at                    DATETIME      NOT NULL,
    updated_at                    DATETIME      NOT NULL,
    authorized_scopes             VARCHAR(1000) NULL,
    attributes                    TEXT          NULL,
    state                         VARCHAR(500)  NULL,
    authorization_code_value      TEXT          NULL,
    authorization_code_issued_at  DATETIME      NULL,
    authorization_code_expires_at DATETIME      NULL,
    authorization_code_metadata   TEXT          NULL,
    access_token_value            TEXT          NULL,
    access_token_issued_at        DATETIME      NULL,
    access_token_expires_at       DATETIME      NULL,
    access_token_metadata         TEXT          NULL,
    access_token_type             VARCHAR(255)  NULL,
    access_token_scopes           VARCHAR(1000) NULL,
    refresh_token_value           TEXT          NULL,
    refresh_token_issued_at       DATETIME      NULL,
    refresh_token_expires_at      DATETIME      NULL,
    refresh_token_metadata        TEXT          NULL,
    oidc_id_token_value           TEXT          NULL,
    oidc_id_token_issued_at       DATETIME      NULL,
    oidc_id_token_expires_at      DATETIME      NULL,
    oidc_id_token_metadata        TEXT          NULL,
    oidc_id_token_claims          TEXT          NULL,
    user_code_value               TEXT          NULL,
    user_code_issued_at           DATETIME      NULL,
    user_code_expires_at          DATETIME      NULL,
    user_code_metadata            TEXT          NULL,
    device_code_value             TEXT          NULL,
    device_code_issued_at         DATETIME      NULL,
    device_code_expires_at        DATETIME      NULL,
    device_code_metadata          TEXT          NULL,
    CONSTRAINT pk_authorization PRIMARY KEY (id)
);

-- 3. Authorization Consent Table
CREATE TABLE `authorization_consent`
(
    registered_client_id VARCHAR(255) NOT NULL,
    principal_name       VARCHAR(255) NOT NULL,
    authorities          TEXT         NOT NULL,
    created_at           DATETIME     NOT NULL,
    updated_at           DATETIME     NOT NULL,
    CONSTRAINT pk_authorizationconsent PRIMARY KEY (registered_client_id, principal_name)
);

-- 4. Utility Updates (Indices and Modifications)
CREATE INDEX idx_auth_client_id ON `authorization` (registered_client_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_phone_number UNIQUE (phone_number);

-- Sync existing tables auditing fields to match the new DATETIME standard
ALTER TABLE roles MODIFY created_at DATETIME NOT NULL;
ALTER TABLE roles MODIFY updated_at DATETIME NOT NULL;
ALTER TABLE users MODIFY created_at DATETIME NOT NULL;
ALTER TABLE users MODIFY updated_at DATETIME NOT NULL;