CREATE TABLE roles
(
    id            BINARY(16)   NOT NULL,
    created_at    datetime    NOT NULL,
    updated_at    datetime    NOT NULL,
    is_deleted    BIT(1)      NOT NULL,
    name          VARCHAR(50) NOT NULL,
    `description` VARCHAR(255) NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE sessions
(
    id          BINARY(16)    NOT NULL,
    created_at  datetime      NOT NULL,
    updated_at  datetime      NOT NULL,
    is_deleted  BIT(1)        NOT NULL,
    token       VARCHAR(1000) NOT NULL,
    expiring_at datetime      NOT NULL,
    user_id     BINARY(16)    NOT NULL,
    status      VARCHAR(20)   NOT NULL,
    CONSTRAINT pk_sessions PRIMARY KEY (id)
);

CREATE TABLE user_roles
(
    role_id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (role_id, user_id)
);

CREATE TABLE users
(
    id                BINARY(16)   NOT NULL,
    created_at        datetime     NOT NULL,
    updated_at        datetime     NOT NULL,
    is_deleted        BIT(1)       NOT NULL,
    email             VARCHAR(255) NOT NULL,
    password          VARCHAR(255) NOT NULL,
    username          VARCHAR(50) NULL,
    phone_number      VARCHAR(20) NULL,
    is_email_verified BIT(1)       NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE roles
    ADD CONSTRAINT uc_roles_name UNIQUE (name);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_phone_number UNIQUE (phone_number);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

CREATE INDEX idx_user_email ON users (email);

CREATE INDEX idx_user_username ON users (username);

ALTER TABLE sessions
    ADD CONSTRAINT FK_SESSION_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_usercauV8z FOREIGN KEY (role_id) REFERENCES roles (id);
CREATE TABLE roles
(
    id            BINARY(16)   NOT NULL,
    created_at    datetime     NOT NULL,
    updated_at    datetime     NOT NULL,
    is_deleted    BIT(1)       NOT NULL,
    name          VARCHAR(50)  NOT NULL,
    `description` VARCHAR(255) NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE sessions
(
    id          BINARY(16)    NOT NULL,
    created_at  datetime      NOT NULL,
    updated_at  datetime      NOT NULL,
    is_deleted  BIT(1)        NOT NULL,
    token       VARCHAR(1000) NOT NULL,
    expiring_at datetime      NOT NULL,
    user_id     BINARY(16)    NOT NULL,
    status      VARCHAR(20)   NOT NULL,
    CONSTRAINT pk_sessions PRIMARY KEY (id)
);

CREATE TABLE user_roles
(
    role_id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (role_id, user_id)
);

CREATE TABLE users
(
    id                BINARY(16)   NOT NULL,
    created_at        datetime     NOT NULL,
    updated_at        datetime     NOT NULL,
    is_deleted        BIT(1)       NOT NULL,
    email             VARCHAR(255) NOT NULL,
    password          VARCHAR(255) NOT NULL,
    username          VARCHAR(50)  NULL,
    phone_number      VARCHAR(20)  NULL,
    is_email_verified BIT(1)       NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE roles
    ADD CONSTRAINT uc_roles_name UNIQUE (name);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_phone_number UNIQUE (phone_number);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

CREATE INDEX idx_user_email ON users (email);

CREATE INDEX idx_user_username ON users (username);

ALTER TABLE sessions
    ADD CONSTRAINT FK_SESSION_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_uservWxUWV FOREIGN KEY (role_id) REFERENCES roles (id);
CREATE TABLE roles
(
    id            BINARY(16)   NOT NULL,
    created_at    datetime     NOT NULL,
    updated_at    datetime     NOT NULL,
    is_deleted    BIT(1)       NOT NULL,
    name          VARCHAR(50)  NOT NULL,
    `description` VARCHAR(255) NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE sessions
(
    id          BINARY(16)    NOT NULL,
    created_at  datetime      NOT NULL,
    updated_at  datetime      NOT NULL,
    is_deleted  BIT(1)        NOT NULL,
    token       VARCHAR(1000) NOT NULL,
    expiring_at datetime      NOT NULL,
    user_id     BINARY(16)    NOT NULL,
    status      VARCHAR(20)   NOT NULL,
    CONSTRAINT pk_sessions PRIMARY KEY (id)
);

CREATE TABLE user_roles
(
    role_id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (role_id, user_id)
);

CREATE TABLE users
(
    id                BINARY(16)   NOT NULL,
    created_at        datetime     NOT NULL,
    updated_at        datetime     NOT NULL,
    is_deleted        BIT(1)       NOT NULL,
    email             VARCHAR(255) NOT NULL,
    password          VARCHAR(255) NOT NULL,
    username          VARCHAR(50)  NULL,
    phone_number      VARCHAR(20)  NULL,
    is_email_verified BIT(1)       NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE roles
    ADD CONSTRAINT uc_roles_name UNIQUE (name);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_phone_number UNIQUE (phone_number);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

CREATE INDEX idx_user_email ON users (email);

CREATE INDEX idx_user_username ON users (username);

ALTER TABLE sessions
    ADD CONSTRAINT FK_SESSION_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_userJk8naN FOREIGN KEY (role_id) REFERENCES roles (id);